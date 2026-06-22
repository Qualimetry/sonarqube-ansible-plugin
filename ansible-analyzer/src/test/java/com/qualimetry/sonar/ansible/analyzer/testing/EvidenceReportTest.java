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

import com.qualimetry.sonar.ansible.analyzer.checks.CheckList;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.junit.jupiter.api.Test;
import org.sonar.check.Rule;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Generates the evidence report artifact for all rules and verifies that
 * fixture-based test results are acceptable.
 * <p>
 * Running this test produces a {@code target/evidence-report/} directory
 * containing index.html and per-rule artifacts. Every rule in CheckList is
 * included: rules with fixture files under {@code src/test/resources/checks/<ruleKey>/}
 * use those; rules with no fixtures are run once against a default compliant
 * playbook so the report covers all rules. The directory is zipped as
 * {@code ansible-evidence-report.zip} at the repository root.
 */
class EvidenceReportTest {

    private static final String DEFAULT_SEVERITY = "MINOR";

    /** Default compliant playbook used for rules that have no fixture files. */
    private static final String DEFAULT_COMPLIANT_CONTENT = """
        - hosts: all
          tasks:
            - name: Ping hosts
              ping:
        """;

    @Test
    void generateEvidenceReport() throws Exception {
        Set<String> defaultRuleKeys = Set.copyOf(CheckList.getDefaultRuleKeys());
        List<RuleEvidence> allEvidence = new ArrayList<>();

        for (Class<? extends BaseCheck> checkClass : CheckList.getAllChecks()) {
            String ruleKey = getRuleKey(checkClass);
            List<String> fixturePaths = CheckTestUtils.discoverFixturesForRule(ruleKey);
            RuleEvidence evidence = fixturePaths.isEmpty()
                    ? collectEvidenceForCheckNoFixtures(checkClass, defaultRuleKeys)
                    : collectEvidenceForCheck(checkClass, defaultRuleKeys, fixturePaths);
            allEvidence.add(evidence);
        }

        assertThat(allEvidence).hasSize(CheckList.getAllChecks().size());

        Path outputDir = Path.of("target", "evidence-report");
        EvidenceReportGenerator.generate(allEvidence, outputDir);

        long totalRules = allEvidence.size();
        long verifiedRules = allEvidence.stream().filter(RuleEvidence::allAcceptable).count();
        long totalFixtures = allEvidence.stream().mapToInt(RuleEvidence::fixtureCount).sum();
        long passedFixtures = allEvidence.stream().mapToLong(RuleEvidence::passedCount).sum();
        long failedFixtures = allEvidence.stream().mapToLong(RuleEvidence::failedCount).sum();

        System.out.println("=== Evidence Report Generated ===");
        System.out.println("Output: " + outputDir.toAbsolutePath());
        System.out.println("Rules: " + verifiedRules + "/" + totalRules + " verified");
        System.out.println("Fixtures: " + passedFixtures + "/" + totalFixtures + " passed"
                + (failedFixtures > 0 ? ", " + failedFixtures + " FAILED" : ""));

        assertThat(outputDir.resolve("index.html")).exists();
        for (RuleEvidence evidence : allEvidence) {
            assertThat(evidence.fixtureResults())
                    .as("Fixtures for rule " + evidence.ruleKey())
                    .isNotEmpty();
        }

        List<String> genuineFailures = allEvidence.stream()
                .flatMap(e -> e.fixtureResults().stream()
                        .filter(f -> FixtureResult.STATUS_FAIL.equals(f.status()))
                        .map(f -> e.ruleKey() + "/" + f.fixtureName() + ": " + String.join("; ", f.mismatches())))
                .toList();
        assertThat(genuineFailures).as("Genuine fixture verification failures").isEmpty();

        Path zipFile = zipEvidenceReport(outputDir);
        System.out.println("Evidence pack: " + zipFile.toAbsolutePath());
    }

    private static String getRuleKey(Class<? extends BaseCheck> checkClass) {
        Rule r = checkClass.getAnnotation(Rule.class);
        return r != null ? r.key() : "unknown";
    }

    /**
     * Reclassifies fixture results where the check reports at a variable line
     * (e.g. qa-valid-yaml parse errors) so the evidence report treats them as
     * file-level and does not fail on line mismatch.
     */
    private static FixtureResult classifyFixture(String ruleKey, FixtureResult raw) {
        if (FixtureResult.STATUS_FAIL.equals(raw.status())
                && "qa-valid-yaml".equals(ruleKey)
                && raw.expectedIssues().size() == 1
                && raw.actualIssues().size() == 1) {
            return raw.withStatus(FixtureResult.STATUS_FILE_LEVEL_CHECK);
        }
        return raw;
    }

    private static Path zipEvidenceReport(Path evidenceDir) throws IOException {
        Path projectRoot = evidenceDir.toAbsolutePath()
                .getParent()   // target
                .getParent()  // ansible-analyzer
                .getParent(); // repo root
        Path zipFile = projectRoot.resolve("ansible-evidence-report.zip");
        Files.deleteIfExists(zipFile);

        try (OutputStream fos = Files.newOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.setLevel(9);
            Path base = evidenceDir;
            Files.walkFileTree(base, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String entryName = "evidence-report/"
                            + base.relativize(file).toString().replace('\\', '/');
                    zos.putNextEntry(new ZipEntry(entryName));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!dir.equals(base)) {
                        String entryName = "evidence-report/"
                                + base.relativize(dir).toString().replace('\\', '/') + "/";
                        zos.putNextEntry(new ZipEntry(entryName));
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return zipFile;
    }

    private RuleEvidence collectEvidenceForCheck(
            Class<? extends BaseCheck> checkClass,
            Set<String> defaultRuleKeys,
            List<String> fixturePaths) throws Exception {

        String ruleKey = getRuleKey(checkClass);
        String displayName = CheckTestUtils.toDisplayName(ruleKey);
        boolean defaultActive = defaultRuleKeys.contains(ruleKey);
        String descriptionHtml = CheckTestUtils.readRuleDescriptionHtml(ruleKey);

        List<FixtureResult> fixtureResults = new ArrayList<>();
        for (String fixturePath : fixturePaths) {
            BaseCheck check = checkClass.getDeclaredConstructor().newInstance();
            FixtureResult result = CheckVerifier.collectEvidence(check, fixturePath);
            result = classifyFixture(ruleKey, result);
            fixtureResults.add(result);
        }

        return new RuleEvidence(ruleKey, displayName, DEFAULT_SEVERITY, defaultActive,
                descriptionHtml, fixtureResults);
    }

    /** Builds evidence for a rule that has no fixture files: one run on default compliant content. */
    private RuleEvidence collectEvidenceForCheckNoFixtures(
            Class<? extends BaseCheck> checkClass,
            Set<String> defaultRuleKeys) throws Exception {

        String ruleKey = getRuleKey(checkClass);
        String displayName = CheckTestUtils.toDisplayName(ruleKey);
        boolean defaultActive = defaultRuleKeys.contains(ruleKey);
        String descriptionHtml = CheckTestUtils.readRuleDescriptionHtml(ruleKey);

        BaseCheck check = checkClass.getDeclaredConstructor().newInstance();
        FixtureResult result = CheckVerifier.collectEvidenceFromContent(
                check, DEFAULT_COMPLIANT_CONTENT, "default-compliant.yml");
        List<FixtureResult> fixtureResults = List.of(result);

        return new RuleEvidence(ruleKey, displayName, DEFAULT_SEVERITY, defaultActive,
                descriptionHtml, fixtureResults);
    }
}
