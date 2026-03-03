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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sonar.api.server.rule.RulesDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class AnsibleRulesDefinitionTest {

    private static RulesDefinition.Repository repository;

    @BeforeAll
    static void setUp() {
        RulesDefinition.Context context = new RulesDefinition.Context();
        new AnsibleRulesDefinition().define(context);
        repository = context.repository(CheckList.REPOSITORY_KEY);
    }

    @Test
    void shouldCreateRepository() {
        assertThat(repository).isNotNull();
    }

    @Test
    void shouldHaveCorrectRepositoryKey() {
        assertThat(repository.key()).isEqualTo("qualimetry-ansible");
    }

    @Test
    void shouldHaveCorrectRepositoryName() {
        assertThat(repository.name()).isEqualTo("Qualimetry Ansible");
    }

    @Test
    void shouldHaveCorrectLanguage() {
        assertThat(repository.language()).isEqualTo("ansible");
    }

    @Test
    void shouldLoadAllRules() {
        assertThat(repository.rules()).hasSize(75);
    }

    @Test
    void shouldHaveHtmlDescriptionForEveryRule() {
        for (RulesDefinition.Rule rule : repository.rules()) {
            assertThat(rule.htmlDescription())
                    .as("Missing HTML description for rule: " + rule.key())
                    .isNotNull()
                    .isNotEmpty();
        }
    }

    @Test
    void shouldHaveSeverityForEveryRule() {
        for (RulesDefinition.Rule rule : repository.rules()) {
            assertThat(rule.severity())
                    .as("Missing severity for rule: " + rule.key())
                    .isNotNull()
                    .isNotEmpty();
        }
    }

    @Test
    void shouldHaveAnsibleTagForEveryRule() {
        for (RulesDefinition.Rule rule : repository.rules()) {
            assertThat(rule.tags())
                    .as("Rule should have 'ansible' tag: " + rule.key())
                    .isNotNull()
                    .isNotEmpty()
                    .contains("ansible");
        }
    }

    @Test
    void shouldHaveHumanReadableNameForEveryRule() {
        for (RulesDefinition.Rule rule : repository.rules()) {
            assertThat(rule.name())
                    .as("Rule name should be human-readable: " + rule.key())
                    .isNotNull()
                    .isNotEqualTo(rule.key());
            assertThat(rule.name())
                    .as("Rule name should be a multi-word title: " + rule.key())
                    .contains(" ");
        }
    }

    @Test
    void shouldHaveBlockerSeverityForSecretsRules() {
        assertThat(repository.rule("qa-no-log-secrets").severity()).isEqualTo("BLOCKER");
        assertThat(repository.rule("qa-secrets-not-in-vars").severity()).isEqualTo("BLOCKER");
    }

    @Test
    void shouldHaveCriticalSeverityForSecurityRules() {
        assertThat(repository.rule("qa-require-https").severity()).isEqualTo("CRITICAL");
        assertThat(repository.rule("qa-restrict-world-write").severity()).isEqualTo("CRITICAL");
        assertThat(repository.rule("qa-become-non-root-user").severity()).isEqualTo("CRITICAL");
        assertThat(repository.rule("qa-secrets-in-vault").severity()).isEqualTo("CRITICAL");
    }

    @Test
    void shouldHaveMajorSeverityForReliabilityRules() {
        assertThat(repository.rule("qa-valid-yaml").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("qa-yaml-parse-error").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("qa-explicit-error-handling").severity()).isEqualTo("MAJOR");
        assertThat(repository.rule("qa-define-referenced-vars").severity()).isEqualTo("MAJOR");
    }

    @Test
    void shouldHaveMinorSeverityForConventionRules() {
        assertThat(repository.rule("qa-task-name-first").severity()).isEqualTo("MINOR");
        assertThat(repository.rule("qa-even-spaces-indent").severity()).isEqualTo("MINOR");
        assertThat(repository.rule("qa-full-module-name").severity()).isEqualTo("MINOR");
    }

    @Test
    void shouldHaveInfoSeverityForFormattingRules() {
        assertThat(repository.rule("qa-spaces-not-tabs").severity()).isEqualTo("INFO");
        assertThat(repository.rule("qa-file-ends-newline").severity()).isEqualTo("INFO");
        assertThat(repository.rule("qa-strip-trailing-whitespace").severity()).isEqualTo("INFO");
        assertThat(repository.rule("qa-max-line-length").severity()).isEqualTo("INFO");
    }

    @Test
    void securityRuleShouldHaveVulnerabilityType() {
        RulesDefinition.Rule rule = repository.rule("qa-no-log-secrets");
        assertThat(rule).isNotNull();
        assertThat(rule.type()).isEqualTo(org.sonar.api.rules.RuleType.VULNERABILITY);
    }

    @Test
    void securityRuleShouldHaveCweTag() {
        RulesDefinition.Rule rule = repository.rule("qa-no-log-secrets");
        assertThat(rule).isNotNull();
        assertThat(rule.tags()).contains("cwe");
    }

    @Test
    void bugRuleShouldHaveBugType() {
        RulesDefinition.Rule rule = repository.rule("qa-valid-yaml");
        assertThat(rule).isNotNull();
        assertThat(rule.type()).isEqualTo(org.sonar.api.rules.RuleType.BUG);
    }

    @Test
    void codeSmellRuleShouldHaveCodeSmellType() {
        RulesDefinition.Rule rule = repository.rule("qa-full-module-name");
        assertThat(rule).isNotNull();
        assertThat(rule.type()).isEqualTo(org.sonar.api.rules.RuleType.CODE_SMELL);
    }

    @Test
    void conventionRuleShouldHaveConventionTag() {
        RulesDefinition.Rule rule = repository.rule("qa-spaces-not-tabs");
        assertThat(rule).isNotNull();
        assertThat(rule.tags()).contains("convention");
    }
}
