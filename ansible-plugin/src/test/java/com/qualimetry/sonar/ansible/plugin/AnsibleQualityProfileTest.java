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
import org.junit.jupiter.api.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.BuiltInQualityProfile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnsibleQualityProfileTest {

    @Test
    void shouldCreateDefaultProfile() {
        BuiltInQualityProfilesDefinition.Context context = defineProfiles();

        BuiltInQualityProfile profile = context.profile("ansible", "Qualimetry Ansible");
        assertThat(profile).isNotNull();
        assertThat(profile.name()).isEqualTo("Qualimetry Ansible");
        assertThat(profile.language()).isEqualTo("ansible");
    }

    @Test
    void defaultProfileShouldNotBeDefault() {
        BuiltInQualityProfilesDefinition.Context context = defineProfiles();

        BuiltInQualityProfile profile = context.profile("ansible", "Qualimetry Ansible");
        assertThat(profile.isDefault()).isFalse();
    }

    @Test
    void defaultProfileShouldActivate47Rules() {
        BuiltInQualityProfilesDefinition.Context context = defineProfiles();

        BuiltInQualityProfile profile = context.profile("ansible", "Qualimetry Ansible");
        assertThat(profile.rules()).hasSize(47);
    }

    @Test
    void defaultProfileRulesShouldBelongToCorrectRepository() {
        BuiltInQualityProfilesDefinition.Context context = defineProfiles();

        BuiltInQualityProfile profile = context.profile("ansible", "Qualimetry Ansible");
        assertThat(profile.rules()).allSatisfy(rule ->
                assertThat(rule.repoKey()).isEqualTo(CheckList.REPOSITORY_KEY));
    }

    @Test
    void shouldCreateQualimetryWayProfile() {
        BuiltInQualityProfilesDefinition.Context context = defineProfiles();

        BuiltInQualityProfile profile = context.profile("ansible", "Qualimetry Way");
        assertThat(profile).isNotNull();
        assertThat(profile.name()).isEqualTo("Qualimetry Way");
        assertThat(profile.language()).isEqualTo("ansible");
    }

    @Test
    void qualimetryWayProfileShouldNotBeDefault() {
        BuiltInQualityProfilesDefinition.Context context = defineProfiles();

        BuiltInQualityProfile profile = context.profile("ansible", "Qualimetry Way");
        assertThat(profile.isDefault()).isFalse();
    }

    @Test
    void qualimetryWayProfileShouldActivate64Rules() {
        BuiltInQualityProfilesDefinition.Context context = defineProfiles();

        BuiltInQualityProfile profile = context.profile("ansible", "Qualimetry Way");
        assertThat(profile.rules()).hasSize(64);
    }

    @Test
    void qualimetryWayRulesShouldBelongToCorrectRepository() {
        BuiltInQualityProfilesDefinition.Context context = defineProfiles();

        BuiltInQualityProfile profile = context.profile("ansible", "Qualimetry Way");
        assertThat(profile.rules()).allSatisfy(rule ->
                assertThat(rule.repoKey()).isEqualTo(CheckList.REPOSITORY_KEY));
    }

    @Test
    void qualimetryWayShouldContainAllDefaultRules() {
        BuiltInQualityProfilesDefinition.Context context = defineProfiles();

        BuiltInQualityProfile defaultProfile = context.profile("ansible", "Qualimetry Ansible");
        BuiltInQualityProfile wayProfile = context.profile("ansible", "Qualimetry Way");

        List<String> defaultKeys = defaultProfile.rules().stream()
                .map(BuiltInQualityProfilesDefinition.BuiltInActiveRule::ruleKey)
                .toList();
        List<String> wayKeys = wayProfile.rules().stream()
                .map(BuiltInQualityProfilesDefinition.BuiltInActiveRule::ruleKey)
                .toList();

        assertThat(wayKeys).containsAll(defaultKeys);
    }

    @Test
    void qualimetryWayShouldContainSecurityRulesNotInDefault() {
        BuiltInQualityProfilesDefinition.Context context = defineProfiles();

        BuiltInQualityProfile wayProfile = context.profile("ansible", "Qualimetry Way");
        List<String> wayKeys = wayProfile.rules().stream()
                .map(BuiltInQualityProfilesDefinition.BuiltInActiveRule::ruleKey)
                .toList();

        assertThat(wayKeys).contains(
                "qa-no-log-secrets",
                "qa-secrets-not-in-vars",
                "qa-secrets-in-vault",
                "qa-require-https",
                "qa-become-non-root-user",
                "qa-become-with-user"
        );
    }

    private static BuiltInQualityProfilesDefinition.Context defineProfiles() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        new AnsibleQualityProfile().define(context);
        return context;
    }
}
