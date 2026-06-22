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

import com.qualimetry.sonar.ansible.analyzer.parser.model.ParseError;
import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.AnsibleContext;
import com.qualimetry.sonar.ansible.analyzer.visitor.AnsibleWalker;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class YamlSyntaxCheckTest {

    @Test
    void whenNoParseError_reportsNoIssue() {
        PlaybookFile file = new PlaybookFile(List.of(), "file:///repo/play.yml");
        AnsibleContext context = new AnsibleContext(file);
        YamlSyntaxCheck check = new YamlSyntaxCheck();
        check.setContext(context);
        AnsibleWalker.walk(file, check);
        assertThat(context.getIssues()).isEmpty();
    }

    @Test
    void whenParseErrorWithLine_reportsOneLineIssue() {
        PlaybookFile file = new PlaybookFile(
                List.of(),
                "file:///repo/play.yml",
                new ParseError("expected ',' or ']'", 3));
        AnsibleContext context = new AnsibleContext(file);
        YamlSyntaxCheck check = new YamlSyntaxCheck();
        check.setContext(context);
        AnsibleWalker.walk(file, check);
        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).ruleKey()).isEqualTo("qa-valid-yaml");
        assertThat(context.getIssues().get(0).message()).isEqualTo("expected ',' or ']'");
        assertThat(context.getIssues().get(0).line()).isEqualTo(3);
    }

    @Test
    void whenParseErrorWithZeroLine_reportsOneFileIssue() {
        PlaybookFile file = new PlaybookFile(
                List.of(),
                "file:///repo/play.yml",
                new ParseError("YAML parse error", 0));
        AnsibleContext context = new AnsibleContext(file);
        YamlSyntaxCheck check = new YamlSyntaxCheck();
        check.setContext(context);
        AnsibleWalker.walk(file, check);
        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).ruleKey()).isEqualTo("qa-valid-yaml");
        assertThat(context.getIssues().get(0).message()).isEqualTo("YAML parse error");
        assertThat(context.getIssues().get(0).line()).isNull();
    }
}
