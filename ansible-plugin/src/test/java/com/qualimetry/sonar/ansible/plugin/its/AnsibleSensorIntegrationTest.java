/*
 * Copyright 2026 SHAZAM Analytics Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qualimetry.sonar.ansible.plugin.its;

import com.qualimetry.sonar.ansible.analyzer.checks.CheckList;
import com.qualimetry.sonar.ansible.plugin.AnsiblePluginConstants;
import com.qualimetry.sonar.ansible.plugin.AnsibleSensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.check.Rule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration tests for the SonarQube plugin: sensor runs on real playbook content
 * and the full pipeline (sensor, parser, checks, issues) is asserted.
 */
class AnsibleSensorIntegrationTest {

    private static final String REPO_KEY = CheckList.REPOSITORY_KEY;

    private final List<SavedIssue> savedIssues = new ArrayList<>();
    private SensorContext context;
    private FileSystem fileSystem;
    private CheckFactory checkFactory;

    @BeforeEach
    void setUp() {
        savedIssues.clear();
        context = mockContext();
        fileSystem = mockFileSystem();
        checkFactory = mockCheckFactory();
    }

    @Test
    void playbookWithTabs_reportsNoTabsIssue() throws Exception {
        String content = """
            - hosts: all
            \t  tasks:
              - name: Ping
                ping:
            """;
        InputFile inputFile = mockInputFile("playbook-with-tabs.yml", content);
        when(fileSystem.inputFiles(any())).thenReturn(List.of(inputFile));

        AnsibleSensor sensor = new AnsibleSensor(fileSystem, checkFactory);
        sensor.execute(context);

        assertThat(savedIssues).isNotEmpty();
        assertThat(savedIssues.stream().map(SavedIssue::ruleKey).map(RuleKey::toString))
                .anyMatch(k -> k.contains("qa-spaces-not-tabs"));
        SavedIssue noTabs = savedIssues.stream()
                .filter(i -> i.ruleKey().repository().equals(REPO_KEY) && "qa-spaces-not-tabs".equals(i.ruleKey().rule()))
                .findFirst()
                .orElse(null);
        assertThat(noTabs).isNotNull();
        assertThat(noTabs.message()).contains("tab");
        assertThat(noTabs.line()).isEqualTo(2);
    }

    @Test
    void playbookInvalidYaml_reportsYamlSyntaxIssue() throws Exception {
        String content = """
            - hosts: all
              tasks:
              - name: Bad
                copy: "src=foo
            """;
        InputFile inputFile = mockInputFile("playbook-invalid.yml", content);
        when(fileSystem.inputFiles(any())).thenReturn(List.of(inputFile));

        AnsibleSensor sensor = new AnsibleSensor(fileSystem, checkFactory);
        sensor.execute(context);

        assertThat(savedIssues).isNotEmpty();
        SavedIssue yamlSyntax = savedIssues.stream()
                .filter(i -> "qa-valid-yaml".equals(i.ruleKey().rule()))
                .findFirst()
                .orElse(null);
        assertThat(yamlSyntax).isNotNull();
        assertThat(yamlSyntax.message()).isNotEmpty();
    }

    @Test
    void playbookCompliant_reportsNoIssues() throws Exception {
        String content = """
            - hosts: all
              tags: [test]
              tasks:
                - name: Ping hosts
                  ansible.builtin.ping:
            """;
        InputFile inputFile = mockInputFile("playbook-compliant.yml", content);
        when(fileSystem.inputFiles(any())).thenReturn(List.of(inputFile));

        AnsibleSensor sensor = new AnsibleSensor(fileSystem, checkFactory);
        sensor.execute(context);

        assertThat(savedIssues).isEmpty();
    }

    private SensorContext mockContext() {
        SensorContext ctx = mock(SensorContext.class);
        when(ctx.newIssue()).thenAnswer(inv -> {
            final RuleKey[] ruleKey = new RuleKey[1];
            final String[] message = new String[1];
            final Integer[] line = new Integer[1];
            NewIssue newIssue = mock(NewIssue.class);
            when(newIssue.forRule(any(RuleKey.class))).thenAnswer(inv2 -> {
                ruleKey[0] = inv2.getArgument(0);
                return newIssue;
            });
            NewIssueLocation loc = mock(NewIssueLocation.class);
            when(newIssue.newLocation()).thenReturn(loc);
            when(loc.on(any(InputFile.class))).thenReturn(loc);
            when(loc.message(any())).thenAnswer(inv2 -> {
                message[0] = inv2.getArgument(0);
                return loc;
            });
            when(loc.at(any(TextRange.class))).thenAnswer(inv2 -> {
                TextRange r = inv2.getArgument(0);
                line[0] = r != null ? r.start().line() : null;
                return loc;
            });
            when(newIssue.at(any(NewIssueLocation.class))).thenReturn(newIssue);
            doAnswer(inv2 -> {
                savedIssues.add(new SavedIssue(ruleKey[0], message[0], line[0]));
                return null;
            }).when(newIssue).save();
            return newIssue;
        });
        return ctx;
    }

    private FileSystem mockFileSystem() {
        FileSystem fs = mock(FileSystem.class);
        FilePredicates predicates = mock(FilePredicates.class);
        FilePredicate predicate = mock(FilePredicate.class);
        when(fs.predicates()).thenReturn(predicates);
        when(predicates.and(any(FilePredicate.class), any(FilePredicate.class))).thenReturn(predicate);
        when(predicates.hasType(InputFile.Type.MAIN)).thenReturn(predicate);
        when(predicates.hasLanguage(AnsiblePluginConstants.ANSIBLE_LANGUAGE_KEY)).thenReturn(predicate);
        return fs;
    }

    @SuppressWarnings("unchecked")
    private CheckFactory mockCheckFactory() {
        Set<String> defaultKeys = new HashSet<>(CheckList.getDefaultRuleKeys());
        List<com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck> checkInstances = CheckList.getAllChecks().stream()
                .filter(clazz -> {
                    Rule r = clazz.getAnnotation(Rule.class);
                    return r != null && defaultKeys.contains(r.key());
                })
                .map(clazz -> {
                    try {
                        return (com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck) clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        Checks<com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck> checks = mock(Checks.class);
        when(checks.all()).thenReturn(checkInstances);
        when(checks.ruleKey(any())).thenAnswer(inv -> {
            com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck c = inv.getArgument(0);
            Rule r = c.getClass().getAnnotation(Rule.class);
            String key = r != null ? r.key() : "unknown";
            return RuleKey.of(REPO_KEY, key);
        });
        doReturn(checks).when(checks).addAnnotatedChecks(any(Iterable.class));
        CheckFactory cf = mock(CheckFactory.class);
        when(cf.<com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck>create(REPO_KEY)).thenReturn(checks);
        return cf;
    }

    private record SavedIssue(RuleKey ruleKey, String message, Integer line) {}

    private static InputFile mockInputFile(String filename, String content) throws IOException {
        InputFile inputFile = mock(InputFile.class);
        URI uri = URI.create("file:///its/" + filename);
        when(inputFile.uri()).thenReturn(uri);
        when(inputFile.inputStream()).thenReturn(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        when(inputFile.language()).thenReturn(AnsiblePluginConstants.ANSIBLE_LANGUAGE_KEY);
        when(inputFile.type()).thenReturn(InputFile.Type.MAIN);
        when(inputFile.selectLine(anyInt())).thenAnswer(inv -> {
            int line = inv.getArgument(0);
            org.sonar.api.batch.fs.TextPointer start = mock(org.sonar.api.batch.fs.TextPointer.class);
            when(start.line()).thenReturn(line);
            org.sonar.api.batch.fs.TextPointer end = mock(org.sonar.api.batch.fs.TextPointer.class);
            when(end.line()).thenReturn(line);
            TextRange range = mock(TextRange.class);
            when(range.start()).thenReturn(start);
            when(range.end()).thenReturn(end);
            return range;
        });
        return inputFile;
    }
}
