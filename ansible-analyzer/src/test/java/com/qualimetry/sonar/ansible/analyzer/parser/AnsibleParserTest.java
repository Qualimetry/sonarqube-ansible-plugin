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
package com.qualimetry.sonar.ansible.analyzer.parser;

import com.qualimetry.sonar.ansible.analyzer.parser.model.ParseError;
import com.qualimetry.sonar.ansible.analyzer.parser.model.Play;
import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleRef;
import com.qualimetry.sonar.ansible.analyzer.parser.model.Task;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnsibleParserTest {

    private static final String URI = "file:///repo/playbook.yml";

    private final AnsibleParser parser = new AnsibleParser();

    @Test
    void emptyContent_returnsEmptyPlays() {
        PlaybookFile result = parser.parse(URI, "");
        assertThat(result.plays()).isEmpty();
        assertThat(result.uri()).isEqualTo(URI);
        assertThat(result.parseError()).isNull();
    }

    @Test
    void blankContent_returnsEmptyPlays() {
        PlaybookFile result = parser.parse(URI, "   \n\t  ");
        assertThat(result.plays()).isEmpty();
        assertThat(result.parseError()).isNull();
    }

    @Test
    void nullContent_returnsEmptyPlays() {
        PlaybookFile result = parser.parse(URI, null);
        assertThat(result.plays()).isEmpty();
        assertThat(result.parseError()).isNull();
    }

    @Test
    void invalidYaml_returnsParseErrorAndNoCrash() {
        String invalid = "key: [ unclosed";
        PlaybookFile result = parser.parse(URI, invalid);
        assertThat(result.plays()).isEmpty();
        assertThat(result.parseError()).isNotNull();
        assertThat(result.parseError().message()).isNotBlank();
        assertThat(result.parseError().line()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void invalidYaml_syntaxError_hasReasonableMessage() {
        String invalid = "key: [ unclosed";
        PlaybookFile result = parser.parse(URI, invalid);
        assertThat(result.parseError()).isNotNull();
        assertThat(result.parseError().message()).isNotBlank();
    }

    @Test
    void validMinimalPlaybook_returnsOnePlayWithTasks() {
        String yaml = """
            - name: Test play
              hosts: localhost
              tasks:
                - name: Ping
                  ping:
                - name: Debug
                  debug:
                    msg: hello
            """;
        PlaybookFile result = parser.parse(URI, yaml);
        assertThat(result.parseError()).isNull();
        assertThat(result.plays()).hasSize(1);
        Play play = result.plays().get(0);
        assertThat(play.name()).isEqualTo("Test play");
        assertThat(play.tasks()).hasSize(2);
        assertThat(play.line()).isPositive();

        Task first = play.tasks().get(0);
        assertThat(first.name()).isEqualTo("Ping");
        assertThat(first.moduleKey()).isEqualTo("ping");
        assertThat(first.line()).isPositive();

        Task second = play.tasks().get(1);
        assertThat(second.name()).isEqualTo("Debug");
        assertThat(second.moduleKey()).isEqualTo("debug");
        assertThat(second.line()).isPositive();
    }

    @Test
    void validPlaybookWithRoles_returnsRoles() {
        String yaml = """
            - hosts: all
              roles:
                - common
                - role: app_role
            """;
        PlaybookFile result = parser.parse(URI, yaml);
        assertThat(result.parseError()).isNull();
        assertThat(result.plays()).hasSize(1);
        Play play = result.plays().get(0);
        assertThat(play.roles()).hasSize(2);
        assertThat(play.roles().get(0).roleName()).isEqualTo("common");
        assertThat(play.roles().get(1).roleName()).isEqualTo("app_role");
    }

    @Test
    void validPlaybookWithBlock_flattensTasks() {
        String yaml = """
            - hosts: localhost
              tasks:
                - name: Outer
                  ping:
                - block:
                    - name: In block
                      debug:
                        msg: inner
                - name: After block
                  ping:
            """;
        PlaybookFile result = parser.parse(URI, yaml);
        assertThat(result.parseError()).isNull();
        assertThat(result.plays()).hasSize(1);
        Play play = result.plays().get(0);
        assertThat(play.tasks()).hasSize(3);
        assertThat(play.tasks().get(0).name()).isEqualTo("Outer");
        assertThat(play.tasks().get(1).name()).isEqualTo("In block");
        assertThat(play.tasks().get(2).name()).isEqualTo("After block");
    }
}
