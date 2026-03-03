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

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Checks for YAML/Ansible syntax validity. Reports a single issue when the
 * parser could not parse the file (parseError set on PlaybookFile).
 */
@Rule(key = "qa-valid-yaml")
public class YamlSyntaxCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
        if (file.parseError() != null) {
            int line = file.parseError().line();
            if (line > 0) {
                addLineIssue(line, file.parseError().message());
            } else {
                addFileIssue(file.parseError().message());
            }
        }
    }
}
