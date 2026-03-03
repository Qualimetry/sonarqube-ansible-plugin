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
 * A playbook should not contain an excessive number of plays; consider splitting into multiple files.
 */
@Rule(key = "qa-limit-plays")
public class MaxPlaysPerPlaybookCheck extends BaseCheck {

    private static final int DEFAULT_MAX_PLAYS = 10;

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
        int count = file.plays().size();
        if (count > DEFAULT_MAX_PLAYS) {
            addFileIssue("Playbook has " + count + " plays; consider splitting (max " + DEFAULT_MAX_PLAYS + ").");
        }
    }
}
