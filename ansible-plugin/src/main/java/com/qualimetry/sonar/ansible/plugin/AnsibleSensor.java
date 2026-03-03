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
package com.qualimetry.sonar.ansible.plugin;

import com.qualimetry.sonar.ansible.analyzer.checks.CheckList;
import com.qualimetry.sonar.ansible.analyzer.parser.AnsibleParser;
import com.qualimetry.sonar.ansible.analyzer.parser.RoleMetaParser;
import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleMeta;
import com.qualimetry.sonar.ansible.analyzer.visitor.AnsibleContext;
import com.qualimetry.sonar.ansible.analyzer.visitor.AnsibleWalker;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import com.qualimetry.sonar.ansible.analyzer.visitor.Issue;
import com.qualimetry.sonar.ansible.analyzer.visitor.RoleMetaDetector;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.check.Rule;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Sensor that runs Ansible analysis on files classified as Ansible. */
public class AnsibleSensor implements Sensor {

    private final FileSystem fileSystem;
    private final CheckFactory checkFactory;

    public AnsibleSensor(FileSystem fileSystem, CheckFactory checkFactory) {
        this.fileSystem = fileSystem;
        this.checkFactory = checkFactory;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.onlyOnLanguage(AnsiblePluginConstants.ANSIBLE_LANGUAGE_KEY)
                .name("Ansible Analyzer Sensor")
                .onlyOnFileType(InputFile.Type.MAIN);
    }

    @Override
    public void execute(SensorContext context) {
        Checks<BaseCheck> checks = checkFactory.<BaseCheck>create(CheckList.REPOSITORY_KEY)
                .addAnnotatedChecks((Iterable<?>) CheckList.getAllChecks());

        List<BaseCheck> activeChecks = new ArrayList<>(checks.all());
        if (activeChecks.isEmpty()) {
            return;
        }

        Map<String, RuleKey> ruleKeyMap = new HashMap<>();
        for (BaseCheck check : activeChecks) {
            Rule r = check.getClass().getAnnotation(Rule.class);
            if (r != null) {
                RuleKey rk = checks.ruleKey(check);
                if (rk != null) {
                    ruleKeyMap.put(r.key(), rk);
                }
            }
        }

        AnsibleParser parser = new AnsibleParser();
        RoleMetaParser roleMetaParser = new RoleMetaParser();

        for (InputFile inputFile : fileSystem.inputFiles(
                fileSystem.predicates().and(
                        fileSystem.predicates().hasType(InputFile.Type.MAIN),
                        fileSystem.predicates().hasLanguage(AnsiblePluginConstants.ANSIBLE_LANGUAGE_KEY)))) {

            String uri = inputFile.uri().toString();
            String rawContent;
            try (InputStream is = inputFile.inputStream()) {
                rawContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                continue;
            }

            AnsibleContext ansibleContext;
            String relativePath = inputFile.relativePath();
            if (RoleMetaDetector.isRoleMetaFile(relativePath)) {
                RoleMeta roleMeta = roleMetaParser.parse(uri, rawContent);
                PlaybookFile playbookFile = parser.parse(uri, rawContent);
                ansibleContext = new AnsibleContext(playbookFile, inputFile, rawContent);
                ansibleContext.setPathResolver(new SensorPathResolver(fileSystem, inputFile));
                for (BaseCheck check : activeChecks) {
                    check.setContext(ansibleContext);
                    check.visitRoleMeta(roleMeta);
                }
            } else {
                PlaybookFile playbookFile = parser.parse(uri, rawContent);
                if (playbookFile.plays().isEmpty() && playbookFile.parseError() == null) {
                    continue;
                }
                ansibleContext = new AnsibleContext(playbookFile, inputFile, rawContent);
                ansibleContext.setPathResolver(new SensorPathResolver(fileSystem, inputFile));
                for (BaseCheck check : activeChecks) {
                    check.setContext(ansibleContext);
                    AnsibleWalker.walk(playbookFile, check);
                }
            }

            for (Issue issue : ansibleContext.getIssues()) {
                RuleKey ruleKey = ruleKeyMap.get(issue.ruleKey());
                if (ruleKey == null) continue;
                NewIssue newIssue = context.newIssue().forRule(ruleKey);
                NewIssueLocation loc = newIssue.newLocation().on(inputFile).message(issue.message());
                if (issue.line() != null && issue.line() > 0) {
                    loc.at(inputFile.selectLine(issue.line()));
                }
                newIssue.at(loc);
                newIssue.save();
            }
        }
    }
}
