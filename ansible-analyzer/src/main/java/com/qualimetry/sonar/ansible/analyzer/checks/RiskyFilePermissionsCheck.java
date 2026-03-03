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
import java.util.Set;

/**
 * Avoid overly permissive file modes (e.g. 0777, 0666) in copy, file, or template.
 */
@Rule(key = "qa-restrict-file-mode")
public class RiskyFilePermissionsCheck extends BaseCheck {

    private static final Set<String> FILE_MODULE_KEYS = Set.of("copy", "file", "template", "assemble");

    private static boolean isRiskyMode(Object value) {
        if (value == null) return false;
        if (value instanceof Number n) {
            int m = n.intValue();
            return m == 0777 || m == 0776 || m == 0767 || m == 0666;
        }
        if (value instanceof String s) {
            try {
                int m = Integer.parseInt(s, 8);
                return m == 0777 || m == 0776 || m == 0767 || m == 0666;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitTask(Task task) {
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;
        String actionKey = moduleKey.contains(":") ? moduleKey.substring(0, moduleKey.indexOf(':')) : moduleKey;
        if (!FILE_MODULE_KEYS.contains(actionKey)) return;

        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        Object moduleArgs = attrs.get(actionKey);
        Object mode = moduleArgs instanceof Map ? ((Map<String, Object>) moduleArgs).get("mode") : attrs.get("mode");
        if (isRiskyMode(mode)) {
            addLineIssue(task.line(), "Avoid overly permissive file mode (e.g. 0777, 0666).");
        }
    }
}
