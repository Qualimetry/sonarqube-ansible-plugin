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
package com.qualimetry.sonar.ansible.analyzer.testing;

import com.qualimetry.sonar.ansible.analyzer.parser.AnsibleParser;
import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.AnsibleContext;
import com.qualimetry.sonar.ansible.analyzer.visitor.AnsibleWalker;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import com.qualimetry.sonar.ansible.analyzer.visitor.Issue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Test harness for verifying Ansible checks against fixture YAML files.
 * Fixtures use "# Noncompliant" and "# Noncompliant {{message}}" in comments;
 * the annotation on line N means the expected issue is on line N+1.
 */
public final class CheckVerifier {

    private static final Pattern NONCOMPLIANT_PATTERN = Pattern.compile(
            "#\\s*Noncompliant\\s*(:file)?"
                    + "(?:\\s*\\{\\{([^}]+)\\}\\})?");

    private CheckVerifier() {}

    /**
     * Parses the fixture, runs the check, and returns a FixtureResult for evidence collection.
     */
    public static FixtureResult collectEvidence(BaseCheck check, String fixturePath) {
        String rawContent = readFixture(fixturePath);
        List<TestIssue> expected = extractExpectedIssues(rawContent);

        PlaybookFile playbookFile;
        try {
            playbookFile = new AnsibleParser().parse(fixturePath, rawContent);
        } catch (Exception e) {
            playbookFile = null;
        }

        AnsibleContext context = new AnsibleContext(playbookFile, null, rawContent);
        check.setContext(context);
        if (playbookFile != null) {
            AnsibleWalker.walk(playbookFile, check);
        }

        List<TestIssue> actual = context.getIssues().stream()
                .map(CheckVerifier::toTestIssue)
                .sorted(Comparator.comparingInt(TestIssue::line))
                .toList();

        List<String> mismatches = buildMismatches(expected, actual);
        String status = mismatches.isEmpty()
                ? FixtureResult.STATUS_PASS : FixtureResult.STATUS_FAIL;
        return new FixtureResult(fixturePath, rawContent, expected, actual, status, mismatches);
    }

    /**
     * Runs the check on the given playbook content (no classpath fixture).
     * Used to generate evidence for rules that have no fixture files; expects
     * zero issues (compliant). Virtual path is used as fixture name in the report.
     *
     * @param check      the check to run
     * @param content    raw playbook YAML content
     * @param virtualPath path name for the report (e.g. "default-compliant.yml")
     * @return FixtureResult with expected empty, actual from check; PASS if no issues, else FILE_LEVEL_CHECK
     */
    public static FixtureResult collectEvidenceFromContent(BaseCheck check, String content, String virtualPath) {
        List<TestIssue> expected = List.of();
        PlaybookFile playbookFile;
        try {
            playbookFile = new AnsibleParser().parse("file:///" + virtualPath, content);
        } catch (Exception e) {
            playbookFile = null;
        }
        AnsibleContext context = new AnsibleContext(playbookFile, null, content);
        check.setContext(context);
        if (playbookFile != null) {
            AnsibleWalker.walk(playbookFile, check);
        }
        List<TestIssue> actual = context.getIssues().stream()
                .map(CheckVerifier::toTestIssue)
                .sorted(Comparator.comparingInt(TestIssue::line))
                .toList();
        List<String> mismatches = buildMismatches(expected, actual);
        String status = mismatches.isEmpty()
                ? FixtureResult.STATUS_PASS : FixtureResult.STATUS_FILE_LEVEL_CHECK;
        return new FixtureResult(virtualPath, content, expected, actual, status, mismatches);
    }

    static List<TestIssue> extractExpectedIssues(String rawContent) {
        List<TestIssue> issues = new ArrayList<>();
        String[] lines = rawContent.split("\\r?\\n", -1);

        for (int i = 0; i < lines.length; i++) {
            Matcher matcher = NONCOMPLIANT_PATTERN.matcher(lines[i]);
            if (matcher.find()) {
                boolean fileLevel = matcher.group(1) != null;
                int issueLine = fileLevel ? 0 : i + 2;
                String message = matcher.group(2);
                issues.add(new TestIssue(issueLine, message));
            }
        }
        issues.sort(Comparator.comparingInt(TestIssue::line));
        return issues;
    }

    private static TestIssue toTestIssue(Issue issue) {
        int line = issue.line() != null ? issue.line() : 0;
        return new TestIssue(line, issue.message());
    }

    private static String readFixture(String fixturePath) {
        String resourcePath = fixturePath.startsWith("/") ? fixturePath : "/" + fixturePath;
        try (InputStream input = CheckVerifier.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalArgumentException(
                        "Fixture file not found on classpath: " + fixturePath);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read fixture file: " + fixturePath, e);
        }
    }

    private static List<String> buildMismatches(List<TestIssue> expected, List<TestIssue> actual) {
        List<String> errors = new ArrayList<>();
        Map<Integer, List<TestIssue>> expectedByLine = groupByLine(expected);
        Map<Integer, List<TestIssue>> actualByLine = groupByLine(actual);
        Set<Integer> allLines = new TreeSet<>();
        allLines.addAll(expectedByLine.keySet());
        allLines.addAll(actualByLine.keySet());

        for (int line : allLines) {
            List<TestIssue> expAtLine = expectedByLine.getOrDefault(line, List.of());
            List<TestIssue> actAtLine = actualByLine.getOrDefault(line, List.of());

            if (!expAtLine.isEmpty() && actAtLine.isEmpty()) {
                errors.add("Line " + line + ": expected " + expAtLine.size() + " issue(s) but found none.");
            } else if (expAtLine.isEmpty() && !actAtLine.isEmpty()) {
                for (TestIssue issue : actAtLine) {
                    errors.add("Line " + line + ": unexpected issue: " + issue.message());
                }
            } else {
                if (expAtLine.size() != actAtLine.size()) {
                    errors.add("Line " + line + ": expected " + expAtLine.size()
                            + " issue(s) but found " + actAtLine.size() + ".");
                }
                int checkCount = Math.min(expAtLine.size(), actAtLine.size());
                for (int i = 0; i < checkCount; i++) {
                    TestIssue ex = expAtLine.get(i);
                    TestIssue ac = actAtLine.get(i);
                    if (ex.message() != null && !ex.message().equals(ac.message())) {
                        errors.add("Line " + line + ": expected message \"" + ex.message()
                                + "\" but got \"" + ac.message() + "\".");
                    }
                }
            }
        }
        return errors;
    }

    private static Map<Integer, List<TestIssue>> groupByLine(List<TestIssue> issues) {
        return issues.stream()
                .collect(Collectors.groupingBy(TestIssue::line, TreeMap::new, Collectors.toList()));
    }
}
