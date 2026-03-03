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
package com.qualimetry.sonar.ansible.its;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests verifying the Ansible Analyzer plugin on a running SonarQube server (UAT).
 *
 * <p>Scans two projects: noncompliant (playbooks that trigger rules) and compliant (clean).
 * Both projects are left on the server after the run for study.
 *
 * <p>Run from its/ directory:
 * <pre>
 * mvn verify -Pits -Dsonar.host.url=UAT_URL -Dsonar.token=YOUR_TOKEN
 * </pre>
 */
@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RuleVerificationIT extends IntegrationTestBase {

    private static final String NONCOMPLIANT_KEY = "ansible-its-noncompliant";
    private static final String COMPLIANT_KEY = "ansible-its-compliant";

    @BeforeAll
    void scanProjects() throws Exception {
        assumeServerAvailable();

        // Delete first so projects are recreated clean
        deleteProject(NONCOMPLIANT_KEY);
        deleteProject(COMPLIANT_KEY);

        provisionProject(NONCOMPLIANT_KEY, "Ansible ITS Noncompliant");
        runScan(Path.of("projects/noncompliant"), NONCOMPLIANT_KEY);
        waitForAnalysisToComplete(NONCOMPLIANT_KEY);

        provisionProject(COMPLIANT_KEY, "Ansible ITS Compliant");
        runScan(Path.of("projects/compliant"), COMPLIANT_KEY);
        waitForAnalysisToComplete(COMPLIANT_KEY);
    }

    @Test
    void noncompliantProjectHasIssues() throws Exception {
        int totalIssues = getIssueCount(NONCOMPLIANT_KEY, null);
        assertThat(totalIssues)
                .as("Noncompliant project should have at least one issue")
                .isGreaterThan(0);
    }

    @Test
    void compliantProjectHasZeroOrFewIssues() throws Exception {
        int totalIssues = getIssueCount(COMPLIANT_KEY, null);
        assertThat(totalIssues)
                .as("Compliant project should have zero or very few issues")
                .isLessThanOrEqualTo(10);
    }

    @Test
    void noTabsRuleDetectsIssuesInNoncompliant() throws Exception {
        String rule = REPOSITORY_KEY + ":qa-spaces-not-tabs";
        int count = getIssueCount(NONCOMPLIANT_KEY, rule);
        assertThat(count)
                .as("Rule qa-spaces-not-tabs should detect at least one issue in noncompliant project")
                .isGreaterThan(0);
    }

    @Test
    void yamlSyntaxRuleDetectsIssuesInNoncompliant() throws Exception {
        String rule = REPOSITORY_KEY + ":qa-valid-yaml";
        int count = getIssueCount(NONCOMPLIANT_KEY, rule);
        assertThat(count)
                .as("Rule qa-valid-yaml should detect at least one issue in noncompliant project")
                .isGreaterThan(0);
    }

    @Test
    void trailingWhitespaceRuleDetectsIssuesInNoncompliant() throws Exception {
        String rule = REPOSITORY_KEY + ":qa-strip-trailing-whitespace";
        int count = getIssueCount(NONCOMPLIANT_KEY, rule);
        assertThat(count)
                .as("Rule qa-strip-trailing-whitespace should detect at least one issue in noncompliant project")
                .isGreaterThan(0);
    }

    @Test
    void taskHasNameRuleDetectsIssuesInNoncompliant() throws Exception {
        String rule = REPOSITORY_KEY + ":qa-task-has-name";
        int count = getIssueCount(NONCOMPLIANT_KEY, rule);
        assertThat(count)
                .as("Rule qa-task-has-name should detect at least one issue in noncompliant project")
                .isGreaterThan(0);
    }

    @Test
    void taskNameFirstRuleDetectsIssuesInNoncompliant() throws Exception {
        String rule = REPOSITORY_KEY + ":qa-task-name-first";
        int count = getIssueCount(NONCOMPLIANT_KEY, rule);
        assertThat(count)
                .as("Rule qa-task-name-first should detect at least one issue in noncompliant project")
                .isGreaterThan(0);
    }

    @Test
    void delegateToLocalhostRuleDetectsIssuesInNoncompliant() throws Exception {
        String rule = REPOSITORY_KEY + ":qa-delegate-to-localhost";
        int count = getIssueCount(NONCOMPLIANT_KEY, rule);
        assertThat(count)
                .as("Rule qa-delegate-to-localhost should detect at least one issue in noncompliant project")
                .isGreaterThan(0);
    }

    private int getIssueCount(String projectKey, String rule) throws Exception {
        String encodedKey = URLEncoder.encode(projectKey, StandardCharsets.UTF_8);
        StringBuilder path = new StringBuilder("/api/issues/search?componentKeys=");
        path.append(encodedKey);
        path.append("&ps=1&p=1");
        if (rule != null) {
            path.append("&rules=").append(URLEncoder.encode(rule, StandardCharsets.UTF_8));
        }
        JsonObject response = apiGet(path.toString());
        return response.get("total").getAsInt();
    }
}
