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

import com.qualimetry.sonar.ansible.analyzer.parser.model.Task;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Prefer when: var over when: var == "yes" (or "true", "no", "false"); the literal compare is redundant.
 */
@Rule(key = "qa-avoid-literal-bool-compare")
public class LiteralCompareCheck extends BaseCheck {

    private static final Pattern LITERAL_COMPARE = Pattern.compile(
            "==\\s*[\"']?(?:yes|true|no|false)[\"']?|!=\\s*[\"']?(?:yes|true|no|false)[\"']?",
            Pattern.CASE_INSENSITIVE);

    @Override
    public void visitTask(Task task) {
        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        Object whenObj = attrs.get("when");
        if (whenObj == null) return;
        if (whenObj instanceof String s) {
            if (LITERAL_COMPARE.matcher(s).find()) {
                addLineIssue(task.line(), "Prefer when: var over when: var == \"yes\" (or similar literal).");
            }
        } else if (whenObj instanceof java.util.List<?> list) {
            for (Object item : list) {
                if (item instanceof String str && LITERAL_COMPARE.matcher(str).find()) {
                    addLineIssue(task.line(), "Prefer when: var over when: var == \"yes\" (or similar literal).");
                    return;
                }
            }
        }
    }
}
