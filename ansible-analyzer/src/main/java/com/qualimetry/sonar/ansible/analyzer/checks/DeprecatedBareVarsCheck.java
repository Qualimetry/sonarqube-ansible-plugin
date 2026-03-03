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
 * Avoid bare variable syntax (e.g. when: "{{ x }}") in when/loop; use the bare variable (when: x) instead.
 */
@Rule(key = "qa-bare-var-in-condition")
public class DeprecatedBareVarsCheck extends BaseCheck {

    /** Matches {{ varname }} or {{ varname }} with optional whitespace (bare var only, no filters). */
    private static final Pattern BARE_VAR = Pattern.compile("^\\s*\\{\\{\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\}\\}\\s*$");

    @Override
    public void visitTask(Task task) {
        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;

        checkWhen(attrs, task.line());
        checkLoop(attrs, task.line());
    }

    private void checkWhen(Map<String, Object> attrs, int line) {
        Object when = attrs.get("when");
        if (when instanceof String s && BARE_VAR.matcher(s).matches()) {
            addLineIssue(line, "Use the bare variable in \"when\" (e.g. when: var) instead of when: \"{{ var }}\".");
        }
    }

    private void checkLoop(Map<String, Object> attrs, int line) {
        Object loop = attrs.get("loop");
        if (loop instanceof String s && BARE_VAR.matcher(s).matches()) {
            addLineIssue(line, "Use the bare variable in \"loop\" (e.g. loop: items) instead of loop: \"{{ items }}\".");
        }
    }
}
