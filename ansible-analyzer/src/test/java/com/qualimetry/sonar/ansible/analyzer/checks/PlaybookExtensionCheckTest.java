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
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.AnsibleParser;
import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.AnsibleContext;
import com.qualimetry.sonar.ansible.analyzer.visitor.AnsibleWalker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlaybookExtensionCheckTest {

    private static final String URI = "file:///repo/play.yml";
    private final AnsibleParser parser = new AnsibleParser();

    private AnsibleContext runCheck(PlaybookFile file) {
        AnsibleContext context = new AnsibleContext(file);
        PlaybookExtensionCheck check = new PlaybookExtensionCheck();
        check.setContext(context);
        AnsibleWalker.walk(file, check);
        return context;
    }

    @Test
    void whenUriEndsWithYml_reportsNoIssue() {
        PlaybookFile file = parser.parse("file:///repo/main.yml", "- hosts: all\n  tasks: []");
        assertThat(runCheck(file).getIssues()).isEmpty();
    }

    @Test
    void whenUriEndsWithYaml_reportsNoIssue() {
        PlaybookFile file = parser.parse("file:///repo/main.yaml", "- hosts: all\n  tasks: []");
        assertThat(runCheck(file).getIssues()).isEmpty();
    }

    @Test
    void whenUriHasNoExtension_reportsIssue() {
        PlaybookFile file = parser.parse("file:///repo/playbook", "- hosts: all\n  tasks: []");
        AnsibleContext context = runCheck(file);
        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).ruleKey()).isEqualTo("qa-playbook-yml-extension");
    }

    @Test
    void whenUriEndsWithOtherExtension_reportsIssue() {
        PlaybookFile file = parser.parse("file:///repo/deploy.txt", "- hosts: all\n  tasks: []");
        AnsibleContext context = runCheck(file);
        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).ruleKey()).isEqualTo("qa-playbook-yml-extension");
    }
}
