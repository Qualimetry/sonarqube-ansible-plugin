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

/**
 * ignore_errors: yes suppresses failures; prefer explicit error handling or failed_when.
 */
@Rule(key = "qa-explicit-error-handling")
public class IgnoreErrorsCheck extends BaseCheck {

    private static boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) return "yes".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s);
        return false;
    }

    @Override
    public void visitTask(Task task) {
        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        if (isTruthy(attrs.get("ignore_errors"))) {
            addLineIssue(task.line(), "Avoid \"ignore_errors: yes\"; handle failures explicitly or use failed_when.");
        }
    }
}
