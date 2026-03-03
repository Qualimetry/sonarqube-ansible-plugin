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
 * Avoid vars_prompt; it makes playbooks non-idempotent and blocks automation.
 */
@Rule(key = "qa-no-vars-prompt")
public class NoPromptingCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
        String content = getContext() != null ? getContext().getRawContent() : null;
        if (content == null) return;
        if (content.contains("vars_prompt:") || content.matches("(?m)^\\s*vars_prompt\\s*:")) {
            addFileIssue("Avoid vars_prompt; use extra vars or inventory instead.");
        }
    }
}
