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
 * Prefer when: var over when: var == "" or when: var != "".
 */
@Rule(key = "qa-check-length-not-empty")
public class EmptyStringCompareCheck extends BaseCheck {

    private static final Pattern EMPTY_COMPARE = Pattern.compile("==\\s*[\"']?[\"']\\s*|!=\\s*[\"']?[\"']\\s*", Pattern.CASE_INSENSITIVE);

    @Override
    public void visitTask(Task task) {
        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        Object when = attrs.get("when");
        if (when instanceof String s && EMPTY_COMPARE.matcher(s).find()) {
            addLineIssue(task.line(), "Prefer when: var over when: var == \"\" (or != \"\").");
        }
    }
}
