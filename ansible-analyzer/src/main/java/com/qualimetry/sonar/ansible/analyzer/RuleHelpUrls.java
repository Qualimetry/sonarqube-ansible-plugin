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
package com.qualimetry.sonar.ansible.analyzer;

/**
 * Resolves the public documentation URL for a rule key. The target is the
 * per-rule markdown published alongside the SonarQube plugin snapshot, so the
 * SonarQube rule page, the LSP code description, and the IntelliJ quick-fix all
 * point at the same page.
 */
public final class RuleHelpUrls {

    public static final String HELP_BASE =
            "https://github.com/Qualimetry/sonarqube-ansible-plugin/blob/main/docs/rules/";

    private RuleHelpUrls() {
    }

    public static String helpUrl(String ruleKey) {
        return HELP_BASE + ruleKey + ".md";
    }
}
