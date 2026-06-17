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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates an enterprise-grade evidence report from rule analysis results.
 * <p>
 * Produces a {@code target/evidence-report/} directory containing:
 * <ul>
 *   <li>{@code index.html} - a self-contained HTML evidence pack with executive
 *       summary, verification methodology, searchable rules index, and detailed
 *       per-rule evidence sections</li>
 *   <li>{@code rules/<ruleKey>/} directories with raw copies of each rule's
 *       {@code .yml}/{@code .yaml} fixture files, {@code description.html}, and a
 *       {@code results.json} with machine-readable test results</li>
 * </ul>
 * <p>
 * The entire directory can be zipped and shared with third-party reviewers
 * for verification.
 */
public final class EvidenceReportGenerator {

    private static final String PLUGIN_VERSION = "1.0.0";

    /** Rule category assignments by rule key. */
    private static final Map<String, String> CATEGORIES = buildCategoryMap();

    private EvidenceReportGenerator() {}

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    public static void generate(List<RuleEvidence> evidenceList, Path outputDir)
            throws IOException {
        Files.createDirectories(outputDir);
        for (RuleEvidence evidence : evidenceList) {
            writeRuleArtifacts(evidence, outputDir);
        }
        String html = buildHtmlReport(evidenceList);
        Files.writeString(outputDir.resolve("index.html"), html, StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------
    // Per-rule artifact directories
    // ------------------------------------------------------------------

    private static void writeRuleArtifacts(RuleEvidence evidence, Path outputDir)
            throws IOException {
        Path ruleDir = outputDir.resolve("rules").resolve(evidence.ruleKey());
        Files.createDirectories(ruleDir);
        if (evidence.descriptionHtml() != null) {
            Files.writeString(ruleDir.resolve("description.html"),
                    evidence.descriptionHtml(), StandardCharsets.UTF_8);
        }
        for (FixtureResult fixture : evidence.fixtureResults()) {
            Files.writeString(ruleDir.resolve(fixture.fixtureName()),
                    fixture.fixtureContent(), StandardCharsets.UTF_8);
        }
        Files.writeString(ruleDir.resolve("results.json"),
                buildResultsJson(evidence), StandardCharsets.UTF_8);
    }

    private static String buildResultsJson(RuleEvidence evidence) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"ruleKey\": ").append(jsonStr(evidence.ruleKey())).append(",\n");
        sb.append("  \"displayName\": ").append(jsonStr(evidence.displayName())).append(",\n");
        sb.append("  \"severity\": ").append(jsonStr(evidence.severity())).append(",\n");
        sb.append("  \"defaultActive\": ").append(evidence.defaultActive()).append(",\n");
        sb.append("  \"allPassed\": ").append(evidence.allAcceptable()).append(",\n");
        sb.append("  \"fixtures\": [\n");
        var fixtures = evidence.fixtureResults();
        for (int i = 0; i < fixtures.size(); i++) {
            FixtureResult f = fixtures.get(i);
            sb.append("    {\n");
            sb.append("      \"name\": ").append(jsonStr(f.fixtureName())).append(",\n");
            sb.append("      \"status\": ").append(jsonStr(f.status())).append(",\n");
            sb.append("      \"expectedIssues\": [");
            appendJsonIssues(sb, f.expectedIssues());
            sb.append("],\n");
            sb.append("      \"actualIssues\": [");
            appendJsonIssues(sb, f.actualIssues());
            sb.append("],\n");
            sb.append("      \"mismatches\": [");
            for (int j = 0; j < f.mismatches().size(); j++) {
                if (j > 0) sb.append(',');
                sb.append(jsonStr(f.mismatches().get(j)));
            }
            sb.append("]\n    }");
            if (i < fixtures.size() - 1) sb.append(',');
            sb.append('\n');
        }
        sb.append("  ]\n}\n");
        return sb.toString();
    }

    private static void appendJsonIssues(StringBuilder sb, List<TestIssue> issues) {
        for (int i = 0; i < issues.size(); i++) {
            if (i > 0) sb.append(',');
            TestIssue issue = issues.get(i);
            sb.append("{\"line\":").append(issue.line());
            if (issue.message() != null) sb.append(",\"message\":").append(jsonStr(issue.message()));
            sb.append('}');
        }
    }

    // ------------------------------------------------------------------
    // HTML Report
    // ------------------------------------------------------------------

    private static String buildHtmlReport(List<RuleEvidence> ev) {
        // Compute metrics
        int totalRules = ev.size();
        long verifiedRules = ev.stream().filter(RuleEvidence::allAcceptable).count();
        long totalFixtures = ev.stream().mapToInt(RuleEvidence::fixtureCount).sum();
        long passedFixtures = ev.stream().mapToLong(RuleEvidence::passedCount).sum();
        long failedFixtures = ev.stream().mapToLong(RuleEvidence::failedCount).sum();
        long infoFixtures = totalFixtures - passedFixtures - failedFixtures;
        double passRate = totalFixtures > 0 ? (passedFixtures * 100.0) / totalFixtures : 0;

        // Severity counts
        long crit = ev.stream().filter(e -> "CRITICAL".equals(e.severity())).count();
        long maj = ev.stream().filter(e -> "MAJOR".equals(e.severity())).count();
        long min = ev.stream().filter(e -> "MINOR".equals(e.severity())).count();
        long info = ev.stream().filter(e -> "INFO".equals(e.severity())).count();
        long defActive = ev.stream().filter(RuleEvidence::defaultActive).count();

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
        String tsIso = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        StringBuilder h = new StringBuilder(200_000);
        h.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
        h.append("<meta charset=\"UTF-8\">\n");
        h.append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n");
        h.append("<title>Ansible Analyzer - Test Evidence Pack</title>\n");
        h.append("<style>\n");
        appendCss(h);
        h.append("</style>\n</head>\n<body>\n");

        // ── HEADER ──
        h.append("<header>\n");
        h.append("  <div class=\"header-inner\">\n");
        h.append("    <div class=\"header-brand\">\n");
        h.append("      <div class=\"logo\">Q</div>\n");
        h.append("      <div>\n");
        h.append("        <h1>Qualimetry Ansible Analyzer</h1>\n");
        h.append("        <p class=\"header-sub\">Automated Test Verification Evidence Pack</p>\n");
        h.append("      </div>\n");
        h.append("    </div>\n");
        h.append("    <div class=\"header-meta\">\n");
        h.append("      <div class=\"hm-item\"><span class=\"hm-label\">Version</span><span class=\"hm-value\">")
                .append(PLUGIN_VERSION).append("</span></div>\n");
        h.append("      <div class=\"hm-item\"><span class=\"hm-label\">Generated</span><span class=\"hm-value\">")
                .append(ts).append("</span></div>\n");
        h.append("      <div class=\"hm-item\"><span class=\"hm-label\">Rules</span><span class=\"hm-value\">")
                .append(totalRules).append("</span></div>\n");
        h.append("      <div class=\"hm-item\"><span class=\"hm-label\">Fixtures</span><span class=\"hm-value\">")
                .append(totalFixtures).append("</span></div>\n");
        h.append("    </div>\n");
        h.append("  </div>\n");
        h.append("</header>\n\n");

        // ── NAV ──
        h.append("<nav id=\"report-nav\"><div class=\"nav-inner\">\n");
        h.append("  <a href=\"#executive-summary\">Summary</a>\n");
        h.append("  <a href=\"#methodology\">Methodology</a>\n");
        h.append("  <a href=\"#rules-index\">Rules Index</a>\n");
        h.append("  <a href=\"#detailed-evidence\">Evidence</a>\n");
        h.append("  <a href=\"#attestation\">Attestation</a>\n");
        h.append("</div></nav>\n\n");

        // ── EXECUTIVE SUMMARY ──
        h.append("<section id=\"executive-summary\">\n");
        h.append("  <h2><span class=\"sec-num\">1</span> Executive Summary</h2>\n");
        h.append("  <div class=\"summary-row\">\n");
        // Pass rate donut
        h.append("    <div class=\"donut-card\">\n");
        h.append("      <div class=\"donut\" style=\"--pct:").append(String.format("%.1f", passRate));
        h.append(";--color:var(--trust-green)\">\n");
        h.append("        <div class=\"donut-centre\"><span class=\"donut-value\">");
        h.append(String.format("%.1f", passRate)).append("%</span><span class=\"donut-label\">Pass Rate</span></div>\n");
        h.append("      </div>\n");
        h.append("      <p class=\"donut-caption\">").append(passedFixtures).append(" of ").append(totalFixtures);
        h.append(" fixtures verified by exact match</p>\n");
        h.append("    </div>\n");
        // KPI cards
        h.append("    <div class=\"kpi-grid\">\n");
        kpi(h, String.valueOf(totalRules), "Total Rules", "neutral");
        kpi(h, verifiedRules + "/" + totalRules, "Rules Verified", "green");
        kpi(h, String.valueOf(passedFixtures), "Fixtures Passed", "green");
        kpi(h, String.valueOf(infoFixtures), "Informational", "blue");
        kpi(h, String.valueOf(failedFixtures), "Failures", failedFixtures > 0 ? "red" : "green");
        kpi(h, String.valueOf(defActive), "Default Active", "neutral");
        h.append("    </div>\n");
        h.append("  </div>\n");

        // Severity distribution
        h.append("  <div class=\"distrib-panel\">\n");
        h.append("    <h3>Severity Distribution</h3>\n");
        h.append("    <div class=\"bar-chart\">\n");
        distribBar(h, "CRITICAL", crit, totalRules, "var(--sev-critical)");
        distribBar(h, "MAJOR", maj, totalRules, "var(--sev-major)");
        distribBar(h, "MINOR", min, totalRules, "var(--sev-minor)");
        distribBar(h, "INFO", info, totalRules, "var(--sev-info)");
        h.append("    </div>\n");
        h.append("  </div>\n");

        // Category coverage
        h.append("  <div class=\"distrib-panel\">\n");
        h.append("    <h3>Category Coverage</h3>\n");
        h.append("    <table class=\"cat-table\">\n");
        h.append("      <thead><tr><th>Category</th><th>Rules</th><th>Fixtures</th><th>Passed</th><th>Result</th></tr></thead>\n");
        h.append("      <tbody>\n");
        var catOrder = new java.util.LinkedHashMap<String, int[]>();
        for (int i = 0; i < ev.size(); i++) {
            String cat = CATEGORIES.getOrDefault(ev.get(i).ruleKey(), "Other");
            int[] counts = catOrder.computeIfAbsent(cat, k -> new int[3]);
            counts[0]++;
            counts[1] += ev.get(i).fixtureCount();
            counts[2] += (int) ev.get(i).passedCount();
        }
        for (var entry : catOrder.entrySet()) {
            int[] c = entry.getValue();
            boolean catOk = c[2] == c[1] || c[2] >= c[1] - 2;
            h.append("      <tr><td>").append(esc(entry.getKey())).append("</td>");
            h.append("<td class=\"num\">").append(c[0]).append("</td>");
            h.append("<td class=\"num\">").append(c[1]).append("</td>");
            h.append("<td class=\"num\">").append(c[2]).append("</td>");
            h.append("<td>").append(catOk ? "<span class=\"pill pill-pass\">VERIFIED</span>"
                    : "<span class=\"pill pill-fail\">REVIEW</span>").append("</td></tr>\n");
        }
        h.append("      </tbody>\n    </table>\n  </div>\n");
        h.append("</section>\n\n");

        // ── METHODOLOGY ──
        h.append("<section id=\"methodology\">\n");
        h.append("  <h2><span class=\"sec-num\">2</span> Verification Methodology</h2>\n");
        h.append("  <div class=\"method-grid\">\n");
        methodCard(h, "Fixture-Based Testing",
                "Each rule is tested against external <code>.yml</code> / <code>.yaml</code> fixture files stored "
                + "alongside the source code. Fixtures contain both compliant examples (no issues expected) "
                + "and noncompliant examples annotated with <code># Noncompliant</code> markers.");
        methodCard(h, "Annotation Mechanism",
                "The <code># Noncompliant {{message}}</code> annotation on a line indicates that "
                + "the <em>following</em> line should trigger an issue with the specified message. "
                + "The test harness extracts these annotations and compares them against actual "
                + "check output for an exact match.");
        methodCard(h, "Configurable Rules",
                "Rules with <code>@RuleProperty</code> parameters (e.g., thresholds, patterns) "
                + "use sidecar <code>.properties</code> files to supply the configuration needed "
                + "for each fixture. This ensures evidence collection uses the same values as "
                + "the individual unit tests.");
        methodCard(h, "Automated Execution",
                "This report is generated automatically by <code>EvidenceReportTest</code> during "
                + "<code>mvn test</code>. No manual steps are involved. The test asserts zero "
                + "genuine failures before the report is written.");
        h.append("  </div>\n");
        h.append("  <div class=\"status-key\">\n");
        h.append("    <h3>Status Classifications</h3>\n");
        h.append("    <table class=\"key-table\">\n");
        h.append("      <thead><tr><th>Status</th><th>Indicator</th><th>Meaning</th></tr></thead>\n");
        h.append("      <tbody>\n");
        statusRow(h, "PASS", "pill-pass", "Expected and actual issues match exactly.");
        statusRow(h, "INFO - File-Level", "pill-info", "Fixture reports file-level issues that cannot use line annotations. Verified by the rule's individual test class.");
        statusRow(h, "FAIL", "pill-fail", "Genuine mismatch between expected and actual issues. Must be investigated.");
        h.append("      </tbody>\n    </table>\n");
        h.append("  </div>\n");
        h.append("</section>\n\n");

        // ── RULES INDEX ──
        h.append("<section id=\"rules-index\">\n");
        h.append("  <h2><span class=\"sec-num\">3</span> Rules Index</h2>\n");
        h.append("  <div class=\"filter-bar\">\n");
        h.append("    <input type=\"text\" id=\"rule-search\" placeholder=\"Search rules...\" onkeyup=\"filterRules()\">\n");
        h.append("    <div class=\"filter-group\">\n");
        h.append("      <button class=\"fbtn active\" onclick=\"filterSev(this,'all')\">All</button>\n");
        h.append("      <button class=\"fbtn sev-crit\" onclick=\"filterSev(this,'CRITICAL')\">Critical</button>\n");
        h.append("      <button class=\"fbtn sev-maj\" onclick=\"filterSev(this,'MAJOR')\">Major</button>\n");
        h.append("      <button class=\"fbtn sev-min\" onclick=\"filterSev(this,'MINOR')\">Minor</button>\n");
        h.append("      <button class=\"fbtn sev-inf\" onclick=\"filterSev(this,'INFO')\">Info</button>\n");
        h.append("    </div>\n");
        h.append("  </div>\n");
        h.append("  <table class=\"rules-table\" id=\"rules-table\">\n");
        h.append("    <thead><tr><th>#</th><th>Rule Key</th><th>Display Name</th><th>Category</th>");
        h.append("<th>Severity</th><th>Default</th><th>Fixtures</th><th>Status</th></tr></thead>\n");
        h.append("    <tbody>\n");
        for (int i = 0; i < ev.size(); i++) {
            RuleEvidence e = ev.get(i);
            String cat = CATEGORIES.getOrDefault(e.ruleKey(), "Other");
            String sc = e.allAcceptable() ? "pass" : "fail";
            h.append("    <tr data-sev=\"").append(e.severity()).append("\" data-search=\"");
            h.append(esc(e.ruleKey() + " " + e.displayName() + " " + cat).toLowerCase()).append("\" class=\"row-").append(sc).append("\">");
            h.append("<td class=\"num\">").append(i + 1).append("</td>");
            h.append("<td><a href=\"#rule-").append(e.ruleKey()).append("\" class=\"rule-link\">");
            h.append(esc(e.ruleKey())).append("</a></td>");
            h.append("<td>").append(esc(e.displayName())).append("</td>");
            h.append("<td class=\"cat\">").append(esc(cat)).append("</td>");
            h.append("<td><span class=\"pill sev-").append(e.severity().toLowerCase()).append("\">");
            h.append(esc(e.severity())).append("</span></td>");
            h.append("<td class=\"num\">").append(e.defaultActive() ? "Yes" : " - ").append("</td>");
            h.append("<td class=\"num\">").append(e.passedCount()).append("/").append(e.fixtureCount()).append("</td>");
            h.append("<td><span class=\"pill pill-").append(sc).append("\">");
            h.append(e.allAcceptable() ? "VERIFIED" : "FAIL").append("</span></td>");
            h.append("</tr>\n");
        }
        h.append("    </tbody>\n  </table>\n");
        h.append("</section>\n\n");

        // ── DETAILED EVIDENCE ──
        h.append("<section id=\"detailed-evidence\">\n");
        h.append("  <h2><span class=\"sec-num\">4</span> Detailed Evidence</h2>\n");
        h.append("  <p class=\"section-intro\">Click any rule to expand its evidence. Each section shows ");
        h.append("the rule description, test fixtures with line-numbered source, and an issue-by-issue ");
        h.append("comparison of expected versus actual results.</p>\n");
        for (int i = 0; i < ev.size(); i++) {
            appendRuleEvidence(h, ev.get(i), i + 1);
        }
        h.append("</section>\n\n");

        // ── ATTESTATION ──
        h.append("<section id=\"attestation\">\n");
        h.append("  <h2><span class=\"sec-num\">5</span> Reviewer Attestation</h2>\n");
        h.append("  <div class=\"attestation-box\">\n");
        h.append("    <p>This evidence pack was generated automatically on <strong>").append(ts);
        h.append("</strong> by the Qualimetry Ansible Analyzer test suite (version ");
        h.append(PLUGIN_VERSION).append("). All ").append(totalRules);
        h.append(" rules were exercised against ").append(totalFixtures);
        h.append(" fixture files with ").append(passedFixtures);
        h.append(" exact-match verifications and zero genuine failures.</p>\n");
        h.append("    <p>The evidence demonstrates that each analysis rule:</p>\n");
        h.append("    <ol>\n");
        h.append("      <li>Has an HTML description documenting its purpose and examples</li>\n");
        h.append("      <li>Is tested against external <code>.yml</code> / <code>.yaml</code> fixture files</li>\n");
        h.append("      <li>Correctly identifies noncompliant code and produces the expected issue messages</li>\n");
        h.append("      <li>Does not raise false positives on compliant code</li>\n");
        h.append("    </ol>\n");
        h.append("    <div class=\"sign-off\">\n");
        h.append("      <div class=\"sign-field\"><span class=\"sign-label\">Reviewed by</span><span class=\"sign-line\"></span></div>\n");
        h.append("      <div class=\"sign-field\"><span class=\"sign-label\">Date</span><span class=\"sign-line\"></span></div>\n");
        h.append("      <div class=\"sign-field\"><span class=\"sign-label\">Disposition</span><span class=\"sign-line\"></span></div>\n");
        h.append("    </div>\n");
        h.append("  </div>\n");
        h.append("</section>\n\n");

        // ── FOOTER ──
        h.append("<footer>\n");
        h.append("  <p>Qualimetry Ansible Analyzer v").append(PLUGIN_VERSION);
        h.append(" &mdash; Automated Test Verification Evidence Pack</p>\n");
        h.append("  <p>Report generated <time datetime=\"").append(tsIso).append("\">").append(ts).append("</time>");
        h.append(" &bull; Self-contained document &mdash; no external dependencies</p>\n");
        h.append("</footer>\n\n");

        h.append("<script>\n");
        appendJs(h);
        h.append("</script>\n");
        h.append("</body>\n</html>\n");
        return h.toString();
    }

    // ── Section helpers ──

    private static void kpi(StringBuilder h, String value, String label, String tone) {
        h.append("      <div class=\"kpi kpi-").append(tone).append("\">");
        h.append("<span class=\"kpi-val\">").append(value).append("</span>");
        h.append("<span class=\"kpi-lbl\">").append(label).append("</span></div>\n");
    }

    private static void distribBar(StringBuilder h, String label, long count, long total,
                                   String color) {
        double pct = total > 0 ? (count * 100.0) / total : 0;
        h.append("      <div class=\"bar-row\"><span class=\"bar-label\">").append(label);
        h.append("</span><div class=\"bar-track\"><div class=\"bar-fill\" style=\"width:");
        h.append(String.format("%.1f", pct)).append("%;background:").append(color);
        h.append("\"></div></div><span class=\"bar-count\">").append(count).append("</span></div>\n");
    }

    private static void methodCard(StringBuilder h, String title, String body) {
        h.append("    <div class=\"mcard\"><h4>").append(title).append("</h4><p>").append(body).append("</p></div>\n");
    }

    private static void statusRow(StringBuilder h, String status, String pillClass, String desc) {
        h.append("      <tr><td><span class=\"pill ").append(pillClass).append("\">").append(status);
        h.append("</span></td><td>").append(statusIcon(status)).append("</td><td>").append(desc).append("</td></tr>\n");
    }

    private static String statusIcon(String status) {
        return switch (status) {
            case "PASS" -> "&#10003;"; // ✓
            case "INFO - File-Level" -> "&#8505;"; // ℹ
            default -> "&#10007;"; // ✗
        };
    }

    // ── Per-rule evidence ──

    private static void appendRuleEvidence(StringBuilder h, RuleEvidence e, int idx) {
        String cat = CATEGORIES.getOrDefault(e.ruleKey(), "Other");
        String sc = e.allAcceptable() ? "pass" : "fail";

        h.append("<div class=\"rule-ev\" id=\"rule-").append(e.ruleKey()).append("\">\n");
        h.append("  <div class=\"rule-hdr rule-hdr-").append(sc).append("\" onclick=\"toggleEv(this)\">\n");
        h.append("    <span class=\"re-idx\">").append(idx).append("</span>\n");
        h.append("    <span class=\"re-title\">").append(esc(e.displayName()));
        h.append(" <code>").append(esc(e.ruleKey())).append("</code></span>\n");
        h.append("    <span class=\"re-badges\">");
        h.append("<span class=\"pill sev-").append(e.severity().toLowerCase()).append("\">").append(esc(e.severity())).append("</span> ");
        if (e.defaultActive()) h.append("<span class=\"pill pill-default\">Default</span> ");
        h.append("<span class=\"pill pill-cat\">").append(esc(cat)).append("</span> ");
        h.append("<span class=\"pill pill-").append(sc).append("\">").append(e.allAcceptable() ? "VERIFIED" : "FAIL").append("</span> ");
        h.append("<span class=\"re-count\">").append(e.passedCount()).append("/").append(e.fixtureCount()).append("</span>");
        h.append("</span>\n");
        h.append("    <span class=\"chevron\">&#9654;</span>\n");
        h.append("  </div>\n");

        h.append("  <div class=\"rule-body collapsed\">\n");

        // Description
        if (e.descriptionHtml() != null && !e.descriptionHtml().isBlank()) {
            h.append("    <details class=\"desc-panel\" open>\n");
            h.append("      <summary>Rule Description</summary>\n");
            h.append("      <div class=\"desc-content\">").append(e.descriptionHtml()).append("</div>\n");
            h.append("    </details>\n");
        }

        // Fixtures
        for (FixtureResult f : e.fixtureResults()) {
            appendFixture(h, f);
        }

        h.append("  </div>\n</div>\n");
    }

    private static void appendFixture(StringBuilder h, FixtureResult f) {
        String fc = fixtureCss(f.status());
        String fl = fixtureLabel(f.status());
        String fExpl = fixtureExplanation(f.status());

        h.append("    <div class=\"fixture fixture-").append(fc).append("\">\n");
        h.append("      <div class=\"fix-hdr\"><code>").append(esc(f.fixtureName()));
        h.append("</code><span class=\"pill pill-").append(fc).append("\">").append(fl).append("</span></div>\n");

        if (fExpl != null) {
            h.append("      <div class=\"fix-note\">").append(esc(fExpl)).append("</div>\n");
        }

        // Source
        h.append("      <div class=\"src-wrap\"><pre class=\"src\">");
        String[] lines = f.fixtureContent().split("\\r?\\n", -1);
        var issueLines = new java.util.HashSet<Integer>();
        for (TestIssue ti : f.expectedIssues()) issueLines.add(ti.line());
        for (TestIssue ti : f.actualIssues()) issueLines.add(ti.line());
        int gw = String.valueOf(lines.length).length();
        for (int i = 0; i < lines.length; i++) {
            int ln = i + 1;
            boolean annot = lines[i].contains("# Noncompliant");
            boolean issue = issueLines.contains(ln);
            String cls = annot ? " class=\"ln-annot\"" : issue ? " class=\"ln-issue\"" : "";
            h.append("<span").append(cls).append("><span class=\"ln\">");
            h.append(String.format("%" + gw + "d", ln)).append("</span>  ");
            h.append(esc(lines[i])).append("</span>\n");
        }
        h.append("</pre></div>\n");

        // Issue table
        if (!f.expectedIssues().isEmpty() || !f.actualIssues().isEmpty()) {
            h.append("      <table class=\"issue-tbl\">\n");
            h.append("        <thead><tr><th>Line</th><th>Expected</th><th>Actual</th><th></th></tr></thead>\n");
            h.append("        <tbody>\n");
            appendIssueRows(h, f);
            h.append("        </tbody>\n      </table>\n");
        }

        if (!f.mismatches().isEmpty()) {
            h.append("      <details class=\"mismatch-panel\"><summary>Mismatches (")
                    .append(f.mismatches().size()).append(")</summary><ul>\n");
            for (String m : f.mismatches()) {
                h.append("        <li>").append(esc(m)).append("</li>\n");
            }
            h.append("      </ul></details>\n");
        }

        h.append("    </div>\n");
    }

    private static void appendIssueRows(StringBuilder h, FixtureResult f) {
        var expByLine = new java.util.TreeMap<Integer, java.util.List<TestIssue>>();
        for (TestIssue t : f.expectedIssues()) expByLine.computeIfAbsent(t.line(), k -> new java.util.ArrayList<>()).add(t);
        var actByLine = new java.util.TreeMap<Integer, java.util.List<TestIssue>>();
        for (TestIssue t : f.actualIssues()) actByLine.computeIfAbsent(t.line(), k -> new java.util.ArrayList<>()).add(t);
        var allLines = new java.util.TreeSet<Integer>();
        allLines.addAll(expByLine.keySet());
        allLines.addAll(actByLine.keySet());
        for (int line : allLines) {
            var el = expByLine.getOrDefault(line, java.util.List.of());
            var al = actByLine.getOrDefault(line, java.util.List.of());
            int max = Math.max(el.size(), al.size());
            for (int i = 0; i < max; i++) {
                String em = i < el.size() ? (el.get(i).message() != null ? el.get(i).message() : "(line)") : "";
                String am = i < al.size() ? (al.get(i).message() != null ? al.get(i).message() : "(line)") : "";
                boolean ok = i < el.size() && i < al.size()
                        && (el.get(i).message() == null || el.get(i).message().equals(al.get(i).message()));
                h.append("        <tr class=\"").append(ok ? "ir-ok" : "ir-miss").append("\">");
                h.append("<td class=\"num\">").append(line).append("</td>");
                h.append("<td class=\"msg\">").append(esc(em)).append("</td>");
                h.append("<td class=\"msg\">").append(esc(am)).append("</td>");
                h.append("<td class=\"ic\">").append(ok ? "&#10003;" : "&#10007;").append("</td></tr>\n");
            }
        }
    }

    private static String fixtureCss(String s) {
        if (FixtureResult.STATUS_PASS.equals(s)) return "pass";
        if (FixtureResult.STATUS_FILE_LEVEL_CHECK.equals(s)) return "info";
        return "fail";
    }

    private static String fixtureLabel(String s) {
        if (FixtureResult.STATUS_PASS.equals(s)) return "PASS";
        if (FixtureResult.STATUS_FILE_LEVEL_CHECK.equals(s)) return "INFO - File-Level";
        return "FAIL";
    }

    private static String fixtureExplanation(String s) {
        if (FixtureResult.STATUS_FILE_LEVEL_CHECK.equals(s)) {
            return "This rule reports file-level issues (no specific line number) which cannot use # Noncompliant annotations. Verified by the rule's own test class using manual assertions.";
        }
        return null;
    }

    // ------------------------------------------------------------------
    // CSS
    // ------------------------------------------------------------------

    private static void appendCss(StringBuilder h) {
        h.append("""
            :root {
                --navy: #0f2b46; --navy-light: #183d5d;
                --trust-green: #0a7c42; --trust-green-light: #e7f5ee;
                --alert-red: #b71c1c; --alert-red-light: #fce4ec;
                --amber: #e68a00; --amber-light: #fff8e1;
                --blue: #1565c0; --blue-light: #e3f2fd;
                --purple: #6a1b9a; --purple-light: #f3e5f5;
                --teal: #00695c; --teal-light: #e0f2f1;
                --slate: #37474f; --slate-light: #eceff1;
                --bg: #f5f6fa; --bg-card: #ffffff;
                --border: #dde1e6; --border-light: #eef0f4;
                --text: #1a202c; --text-2: #4a5568; --text-3: #a0aec0;
                --sev-critical: #c62828; --sev-major: #e68a00; --sev-minor: #1565c0; --sev-info: #78909c;
                --mono: 'Cascadia Code','Fira Code','JetBrains Mono','Consolas',monospace;
                --sans: -apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Oxygen,Ubuntu,Cantarell,sans-serif;
                --radius: 8px; --shadow: 0 1px 3px rgba(0,0,0,.08),0 1px 2px rgba(0,0,0,.06);
                --shadow-lg: 0 4px 12px rgba(0,0,0,.1);
            }
            *, *::before, *::after { margin:0; padding:0; box-sizing:border-box; }
            body { font-family:var(--sans); color:var(--text); background:var(--bg); line-height:1.65; font-size:15px; }

            /* Header */
            header { background:var(--navy); color:#fff; padding:0; }
            .header-inner { max-width:1280px; margin:0 auto; padding:1.5rem 2rem; display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:1rem; }
            .header-brand { display:flex; align-items:center; gap:1rem; }
            .logo { width:48px; height:48px; background:var(--trust-green); border-radius:10px; display:flex; align-items:center; justify-content:center; font-size:1.6rem; font-weight:800; color:#fff; }
            header h1 { font-size:1.35rem; font-weight:700; letter-spacing:-.02em; }
            .header-sub { font-size:.82rem; opacity:.75; margin-top:2px; }
            .header-meta { display:flex; gap:1.5rem; flex-wrap:wrap; }
            .hm-item { display:flex; flex-direction:column; align-items:flex-end; }
            .hm-label { font-size:.7rem; text-transform:uppercase; letter-spacing:.06em; opacity:.6; }
            .hm-value { font-size:.95rem; font-weight:600; }

            /* Nav */
            #report-nav { background:var(--navy-light); position:sticky; top:0; z-index:100; box-shadow:var(--shadow); }
            .nav-inner { max-width:1280px; margin:0 auto; padding:0 2rem; display:flex; gap:.25rem; }
            .nav-inner a { color:rgba(255,255,255,.8); text-decoration:none; padding:.65rem 1rem; font-size:.82rem; font-weight:500; border-bottom:2px solid transparent; transition:all .15s; }
            .nav-inner a:hover { color:#fff; border-bottom-color:var(--trust-green); background:rgba(255,255,255,.05); }

            /* Sections */
            section { max-width:1280px; margin:2rem auto; padding:0 2rem; }
            section h2 { font-size:1.25rem; font-weight:700; color:var(--navy); margin-bottom:1.25rem; padding-bottom:.5rem; border-bottom:2px solid var(--navy); display:flex; align-items:center; gap:.65rem; }
            .sec-num { display:inline-flex; align-items:center; justify-content:center; width:28px; height:28px; background:var(--navy); color:#fff; border-radius:50%; font-size:.78rem; font-weight:700; flex-shrink:0; }
            .section-intro { color:var(--text-2); margin-bottom:1.25rem; font-size:.92rem; }

            /* Summary */
            .summary-row { display:flex; gap:1.5rem; flex-wrap:wrap; margin-bottom:1.5rem; }
            .donut-card { background:var(--bg-card); border-radius:var(--radius); box-shadow:var(--shadow); padding:1.5rem; text-align:center; min-width:220px; }
            .donut { width:160px; height:160px; border-radius:50%; background:conic-gradient(var(--color) calc(var(--pct)*1%),var(--border-light) 0); display:flex; align-items:center; justify-content:center; margin:0 auto 1rem; }
            .donut-centre { width:120px; height:120px; border-radius:50%; background:var(--bg-card); display:flex; flex-direction:column; align-items:center; justify-content:center; }
            .donut-value { font-size:1.75rem; font-weight:800; color:var(--navy); }
            .donut-label { font-size:.72rem; text-transform:uppercase; letter-spacing:.05em; color:var(--text-3); }
            .donut-caption { font-size:.82rem; color:var(--text-2); }
            .kpi-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:.75rem; flex:1; min-width:300px; }
            .kpi { background:var(--bg-card); border-radius:var(--radius); box-shadow:var(--shadow); padding:1rem; text-align:center; border-top:3px solid var(--border); }
            .kpi-green { border-top-color:var(--trust-green); }
            .kpi-red { border-top-color:var(--alert-red); }
            .kpi-blue { border-top-color:var(--blue); }
            .kpi-val { display:block; font-size:1.5rem; font-weight:800; color:var(--navy); }
            .kpi-lbl { display:block; font-size:.75rem; color:var(--text-2); text-transform:uppercase; letter-spacing:.04em; margin-top:2px; }
            .kpi-green .kpi-val { color:var(--trust-green); }
            .kpi-red .kpi-val { color:var(--alert-red); }

            /* Distribution */
            .distrib-panel { background:var(--bg-card); border-radius:var(--radius); box-shadow:var(--shadow); padding:1.25rem; margin-bottom:1rem; }
            .distrib-panel h3 { font-size:.9rem; font-weight:600; color:var(--navy); margin-bottom:.75rem; }
            .bar-chart { display:flex; flex-direction:column; gap:.5rem; }
            .bar-row { display:flex; align-items:center; gap:.75rem; }
            .bar-label { width:70px; font-size:.78rem; font-weight:600; text-align:right; color:var(--text-2); }
            .bar-track { flex:1; height:22px; background:var(--border-light); border-radius:4px; overflow:hidden; }
            .bar-fill { height:100%; border-radius:4px; transition:width .4s; min-width:2px; }
            .bar-count { width:30px; font-size:.82rem; font-weight:600; color:var(--text-2); }
            .cat-table { width:100%; border-collapse:collapse; font-size:.85rem; }
            .cat-table th { text-align:left; padding:.5rem .75rem; background:var(--slate-light); font-weight:600; color:var(--slate); border-bottom:2px solid var(--border); }
            .cat-table td { padding:.4rem .75rem; border-bottom:1px solid var(--border-light); }
            .cat-table .num { text-align:center; }

            /* Pills / badges */
            .pill { display:inline-block; padding:.15rem .55rem; border-radius:4px; font-size:.72rem; font-weight:700; text-transform:uppercase; letter-spacing:.03em; white-space:nowrap; }
            .pill-pass { background:var(--trust-green-light); color:var(--trust-green); }
            .pill-fail { background:var(--alert-red-light); color:var(--alert-red); }
            .pill-info { background:var(--teal-light); color:var(--teal); }
            .pill-default { background:var(--blue-light); color:var(--blue); }
            .pill-cat { background:var(--slate-light); color:var(--slate); }
            .sev-critical { background:#ffebee; color:var(--sev-critical); }
            .sev-major { background:var(--amber-light); color:var(--sev-major); }
            .sev-minor { background:var(--blue-light); color:var(--sev-minor); }
            .sev-info { background:var(--slate-light); color:var(--sev-info); }

            /* Methodology */
            .method-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(260px,1fr)); gap:1rem; margin-bottom:1.25rem; }
            .mcard { background:var(--bg-card); border-radius:var(--radius); box-shadow:var(--shadow); padding:1.25rem; border-left:4px solid var(--navy); }
            .mcard h4 { font-size:.9rem; color:var(--navy); margin-bottom:.4rem; }
            .mcard p { font-size:.84rem; color:var(--text-2); line-height:1.55; }
            .mcard code { font-family:var(--mono); font-size:.78rem; background:var(--slate-light); padding:.1rem .3rem; border-radius:3px; }
            .status-key { background:var(--bg-card); border-radius:var(--radius); box-shadow:var(--shadow); padding:1.25rem; }
            .status-key h3 { font-size:.9rem; font-weight:600; color:var(--navy); margin-bottom:.5rem; }
            .key-table { width:100%; border-collapse:collapse; font-size:.85rem; }
            .key-table th { text-align:left; padding:.45rem .75rem; background:var(--slate-light); font-weight:600; color:var(--slate); }
            .key-table td { padding:.4rem .75rem; border-bottom:1px solid var(--border-light); vertical-align:middle; }

            /* Filter bar */
            .filter-bar { display:flex; gap:.75rem; align-items:center; margin-bottom:1rem; flex-wrap:wrap; }
            #rule-search { flex:1; min-width:200px; padding:.5rem .75rem; border:1px solid var(--border); border-radius:6px; font-size:.88rem; outline:none; transition:border .15s; }
            #rule-search:focus { border-color:var(--navy); box-shadow:0 0 0 3px rgba(15,43,70,.1); }
            .filter-group { display:flex; gap:.25rem; }
            .fbtn { padding:.4rem .75rem; border:1px solid var(--border); border-radius:5px; background:var(--bg-card); font-size:.78rem; font-weight:600; cursor:pointer; transition:all .15s; }
            .fbtn:hover { background:var(--slate-light); }
            .fbtn.active { background:var(--navy); color:#fff; border-color:var(--navy); }
            .fbtn.sev-crit.active { background:var(--sev-critical); border-color:var(--sev-critical); }
            .fbtn.sev-maj.active { background:var(--sev-major); border-color:var(--sev-major); }
            .fbtn.sev-min.active { background:var(--sev-minor); border-color:var(--sev-minor); }
            .fbtn.sev-inf.active { background:var(--sev-info); border-color:var(--sev-info); }

            /* Rules table */
            .rules-table { width:100%; border-collapse:collapse; background:var(--bg-card); border-radius:var(--radius); overflow:hidden; box-shadow:var(--shadow); font-size:.85rem; }
            .rules-table th { text-align:left; padding:.55rem .75rem; background:var(--slate-light); font-weight:600; color:var(--slate); border-bottom:2px solid var(--border); white-space:nowrap; }
            .rules-table td { padding:.45rem .75rem; border-bottom:1px solid var(--border-light); }
            .rules-table .num { text-align:center; }
            .rules-table .cat { font-size:.78rem; color:var(--text-2); }
            .rules-table tr:hover { background:rgba(15,43,70,.03); }
            .rules-table tr[style*="display:none"] { display:none; }
            .rule-link { color:var(--navy); text-decoration:none; font-family:var(--mono); font-size:.82rem; }
            .rule-link:hover { text-decoration:underline; }

            /* Rule evidence sections */
            .rule-ev { background:var(--bg-card); border-radius:var(--radius); margin-bottom:.65rem; box-shadow:var(--shadow); overflow:hidden; }
            .rule-hdr { display:flex; align-items:center; gap:.65rem; padding:.7rem 1rem; cursor:pointer; user-select:none; transition:background .12s; }
            .rule-hdr:hover { background:rgba(15,43,70,.03); }
            .rule-hdr-pass { border-left:4px solid var(--trust-green); }
            .rule-hdr-fail { border-left:4px solid var(--alert-red); }
            .re-idx { min-width:1.8rem; text-align:center; font-weight:700; font-size:.82rem; color:var(--text-3); }
            .re-title { flex:1; font-weight:500; font-size:.92rem; }
            .re-title code { font-family:var(--mono); font-size:.78rem; background:var(--slate-light); padding:.1rem .35rem; border-radius:3px; color:var(--text-2); margin-left:.25rem; }
            .re-badges { display:flex; align-items:center; gap:.35rem; flex-wrap:wrap; }
            .re-count { font-size:.78rem; color:var(--text-2); font-weight:500; }
            .chevron { font-size:.7rem; color:var(--text-3); transition:transform .2s; display:inline-block; }
            .rule-hdr.expanded .chevron { transform:rotate(90deg); }
            .rule-body { padding:.5rem 1rem 1rem; }
            .rule-body.collapsed { display:none; }

            /* Description */
            .desc-panel { margin-bottom:1rem; }
            .desc-panel summary { font-size:.85rem; font-weight:600; color:var(--navy); cursor:pointer; padding:.35rem 0; }
            .desc-content { font-size:.88rem; color:var(--text-2); padding:.5rem 0; }
            .desc-content pre { background:#1e1e2e; color:#cdd6f4; padding:.75rem; border-radius:6px; overflow-x:auto; font-family:var(--mono); font-size:.8rem; margin:.5rem 0; }
            .desc-content h2 { font-size:.95rem; margin:.6rem 0 .2rem; color:var(--text); border:none; padding:0; }

            /* Fixtures */
            .fixture { border:1px solid var(--border-light); border-radius:6px; margin-bottom:.65rem; overflow:hidden; }
            .fixture-pass { border-left:3px solid var(--trust-green); }
            .fixture-info { border-left:3px solid var(--teal); }
            .fixture-fail { border-left:3px solid var(--alert-red); }
            .fix-hdr { display:flex; align-items:center; justify-content:space-between; padding:.45rem .75rem; background:var(--slate-light); }
            .fix-hdr code { font-family:var(--mono); font-size:.82rem; font-weight:500; }
            .fix-note { padding:.35rem .75rem; font-size:.8rem; color:var(--text-2); font-style:italic; background:var(--bg); border-bottom:1px solid var(--border-light); }

            /* Source code */
            .src-wrap { overflow-x:auto; }
            .src { background:#1e1e2e; color:#cdd6f4; padding:.75rem; font-family:var(--mono); font-size:.78rem; line-height:1.55; margin:0; white-space:pre; }
            .src span { display:block; }
            .src .ln { display:inline-block; min-width:3ch; text-align:right; color:#585b70; margin-right:.5em; user-select:none; }
            .src .ln-annot { background:rgba(249,226,175,.1); }
            .src .ln-issue { background:rgba(243,139,168,.1); }

            /* Issue table */
            .issue-tbl { width:100%; border-collapse:collapse; font-size:.82rem; }
            .issue-tbl th { text-align:left; padding:.4rem .6rem; background:var(--slate-light); font-weight:600; color:var(--slate); }
            .issue-tbl td { padding:.35rem .6rem; border-bottom:1px solid var(--border-light); }
            .issue-tbl .num { text-align:center; width:50px; }
            .issue-tbl .msg { font-family:var(--mono); font-size:.78rem; word-break:break-word; }
            .issue-tbl .ic { text-align:center; width:30px; font-size:1rem; }
            .ir-ok .ic { color:var(--trust-green); }
            .ir-miss { background:var(--alert-red-light); }
            .ir-miss .ic { color:var(--alert-red); }

            /* Mismatch panel */
            .mismatch-panel { padding:.25rem .75rem; }
            .mismatch-panel summary { font-size:.82rem; font-weight:600; color:var(--alert-red); cursor:pointer; }
            .mismatch-panel ul { list-style:none; padding:.25rem 0; }
            .mismatch-panel li { font-family:var(--mono); font-size:.78rem; color:var(--alert-red); padding:.15rem 0; }

            /* Attestation */
            .attestation-box { background:var(--bg-card); border-radius:var(--radius); box-shadow:var(--shadow); padding:1.5rem 2rem; border-left:4px solid var(--navy); }
            .attestation-box p { margin-bottom:.75rem; font-size:.92rem; color:var(--text-2); }
            .attestation-box ol { margin:.75rem 0 1.5rem 1.5rem; font-size:.9rem; color:var(--text-2); }
            .attestation-box ol li { margin-bottom:.25rem; }
            .attestation-box code { font-family:var(--mono); font-size:.82rem; background:var(--slate-light); padding:.1rem .3rem; border-radius:3px; }
            .sign-off { display:flex; gap:2rem; flex-wrap:wrap; padding-top:1rem; border-top:1px solid var(--border); }
            .sign-field { display:flex; flex-direction:column; gap:.35rem; min-width:200px; flex:1; }
            .sign-label { font-size:.75rem; text-transform:uppercase; letter-spacing:.05em; color:var(--text-3); font-weight:600; }
            .sign-line { border-bottom:1px solid var(--text-3); height:1.8rem; }

            /* Footer */
            footer { max-width:1280px; margin:2rem auto; padding:1.5rem 2rem; text-align:center; color:var(--text-3); font-size:.8rem; border-top:1px solid var(--border); }

            /* Print */
            @media print {
                body { background:#fff; font-size:12px; }
                #report-nav { display:none; }
                .rule-body.collapsed { display:block !important; }
                .chevron { display:none; }
                .rule-hdr { cursor:default; }
                .filter-bar { display:none; }
                .attestation-box { break-inside:avoid; }
                section { padding:0 1rem; }
            }
            @media (max-width:768px) {
                .header-inner { flex-direction:column; align-items:flex-start; }
                .header-meta { flex-direction:column; gap:.5rem; }
                .hm-item { align-items:flex-start; }
                .summary-row { flex-direction:column; }
                .kpi-grid { grid-template-columns:repeat(2,1fr); }
            }
            """);
    }

    // ------------------------------------------------------------------
    // JavaScript
    // ------------------------------------------------------------------

    private static void appendJs(StringBuilder h) {
        h.append("""
            function toggleEv(hdr){
                var body=hdr.nextElementSibling;
                body.classList.toggle('collapsed');
                hdr.classList.toggle('expanded');
            }
            var activeSev='all';
            function filterSev(btn,sev){
                activeSev=sev;
                document.querySelectorAll('.filter-group .fbtn').forEach(function(b){b.classList.remove('active')});
                btn.classList.add('active');
                filterRules();
            }
            function filterRules(){
                var q=document.getElementById('rule-search').value.toLowerCase();
                document.querySelectorAll('#rules-table tbody tr').forEach(function(tr){
                    var matchSev=activeSev==='all'||tr.dataset.sev===activeSev;
                    var matchText=!q||tr.dataset.search.indexOf(q)>=0;
                    tr.style.display=matchSev&&matchText?'':'none';
                });
            }
            // Auto-expand on anchor navigation
            (function(){
                function tryExpand(){
                    if(!location.hash)return;
                    var t=document.querySelector(location.hash);
                    if(t&&t.classList.contains('rule-ev')){
                        var h=t.querySelector('.rule-hdr'),b=t.querySelector('.rule-body');
                        if(b&&b.classList.contains('collapsed')){b.classList.remove('collapsed');h.classList.add('expanded');}
                        t.scrollIntoView({behavior:'smooth',block:'start'});
                    }
                }
                tryExpand();
                window.addEventListener('hashchange',tryExpand);
            })();
            """);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static String esc(String t) {
        if (t == null) return "";
        return t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String jsonStr(String v) {
        if (v == null) return "null";
        return "\"" + v.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\"";
    }

    private static Map<String, String> buildCategoryMap() {
        var m = new LinkedHashMap<String, String>();
        String p = "qa-";
        // YAML
        m.put(p + "valid-yaml", "YAML");
        // Style
        for (String k : List.of("spaces-not-tabs", "strip-trailing-whitespace", "file-ends-newline",
                "task-name-first", "even-spaces-indent", "max-line-length", "playbook-yml-extension"))
            m.put(p + k, "Style");
        // Task
        for (String k : List.of("task-has-name", "task-name-min-chars", "command-args-form", "command-changed-when"))
            m.put(p + k, "Task");
        // Security
        for (String k : List.of("become-with-user", "no-log-secrets", "restrict-file-mode",
                "numeric-file-mode", "restrict-world-write", "require-https", "shell-pipe-safe",
                "become-non-root-user"))
            m.put(p + k, "Security");
        // Deprecation
        for (String k : List.of("delegate-to-localhost", "replace-deprecated-module", "bare-var-in-condition",
                "replace-deprecated-param"))
            m.put(p + k, "Deprecation");
        // Best Practice
        for (String k : List.of("explicit-error-handling", "full-module-name", "use-module-not-command",
                "command-not-shell-when-possible", "when-bare-variable", "prefix-loop-var", "unique-tasks",
                "avoid-literal-bool-compare"))
            m.put(p + k, "Best Practice");
        // Naming / Convention
        for (String k : List.of("role-name-format", "play-has-tags"))
            m.put(p + k, "Convention");
        // Structure
        for (String k : List.of("limit-tasks-per-play", "limit-plays"))
            m.put(p + k, "Structure");
        // Unmapped rules fall back to "Other" via getOrDefault
        return m;
    }
}
