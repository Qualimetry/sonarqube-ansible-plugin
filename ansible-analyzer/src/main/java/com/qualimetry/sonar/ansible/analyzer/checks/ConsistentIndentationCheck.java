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
 * Use consistent indentation (2 or 4 spaces); do not mix or use odd widths.
 */
@Rule(key = "qa-even-spaces-indent")
public class ConsistentIndentationCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
        String content = getContext().getRawContent();
        if (content == null) return;
        String[] lines = content.split("\\r?\\n", -1);
        Integer indentWidth = null; // 2 or 4
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.stripLeading();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            int spaces = line.length() - trimmed.length();
            if (spaces == 0) continue;
            if (spaces % 2 != 0) {
                addLineIssue(i + 1, "Use an even number of spaces for indentation (2 or 4).");
                continue;
            }
            int width = spaces;
            // Detect step: first non-zero indent or subsequent line with same step
            if (indentWidth == null) {
                indentWidth = (width >= 4 && width % 4 == 0) ? 4 : 2;
            }
            if (width % indentWidth != 0) {
                addLineIssue(i + 1, "Keep indentation consistent (this file uses " + indentWidth + "-space indent).");
            }
        }
    }
}
