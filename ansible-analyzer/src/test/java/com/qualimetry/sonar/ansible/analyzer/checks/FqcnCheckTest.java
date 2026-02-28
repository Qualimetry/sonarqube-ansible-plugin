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

class FqcnCheckTest {

    private static final String URI = "file:///repo/play.yml";
    private final AnsibleParser parser = new AnsibleParser();

    private AnsibleContext runCheck(String yaml) {
        PlaybookFile file = parser.parse(URI, yaml);
        AnsibleContext context = new AnsibleContext(file, null, yaml);
        FqcnCheck check = new FqcnCheck();
        check.setContext(context);
        AnsibleWalker.walk(file, check);
        return context;
    }

    @Test
    void whenFqcnUsed_reportsNoIssue() {
        String yaml = """
            - hosts: all
              tasks:
                - name: Copy file
                  ansible.builtin.copy:
                    src: a.conf
                    dest: /etc/a.conf
            """;
        assertThat(runCheck(yaml).getIssues()).isEmpty();
    }

    @Test
    void whenShortModuleName_reportsIssue() {
        String yaml = """
            - hosts: all
              tasks:
                - name: Copy file
                  copy:
                    src: a.conf
                    dest: /etc/a.conf
            """;
        AnsibleContext context = runCheck(yaml);
        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).ruleKey()).isEqualTo("qa-full-module-name");
        assertThat(context.getIssues().get(0).message()).contains("FQCN");
    }

    @Test
    void whenInclude_reportsNoIssue() {
        String yaml = """
            - hosts: all
              tasks:
                - name: Include tasks
                  include_tasks: other.yml
            """;
        assertThat(runCheck(yaml).getIssues()).isEmpty();
    }

    @Test
    void whenMultipleShortModules_reportsMultipleIssues() {
        String yaml = """
            - hosts: all
              tasks:
                - name: One
                  ping:
                - name: Two
                  debug:
                    msg: hi
            """;
        AnsibleContext context = runCheck(yaml);
        assertThat(context.getIssues()).hasSize(2);
        assertThat(context.getIssues().get(0).ruleKey()).isEqualTo("qa-full-module-name");
        assertThat(context.getIssues().get(1).ruleKey()).isEqualTo("qa-full-module-name");
    }
}
