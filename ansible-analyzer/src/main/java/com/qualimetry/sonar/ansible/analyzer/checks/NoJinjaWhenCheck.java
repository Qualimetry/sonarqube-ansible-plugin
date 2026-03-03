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
 * Do not use Jinja for a simple variable in when; use the bare variable (when: var) instead of when: "{{ var }}".
 */
@Rule(key = "qa-when-bare-variable")
public class NoJinjaWhenCheck extends BaseCheck {

    /** When value is a string that is only {{ varname }} with optional whitespace. */
    private static final Pattern BARE_VAR_IN_WHEN = Pattern.compile("^\\s*\\{\\{\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\}\\}\\s*$");

    @Override
    public void visitTask(Task task) {
        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;

        Object when = attrs.get("when");
        if (when instanceof String s && BARE_VAR_IN_WHEN.matcher(s).matches()) {
            addLineIssue(task.line(), "Do not use Jinja for a simple variable in \"when\"; use when: var instead of when: \"{{ var }}\".");
        }
    }
}
