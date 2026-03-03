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
 * Avoid world-writable file modes (e.g. 0002, 0022) in copy, file, or template.
 */
@Rule(key = "qa-restrict-world-write")
public class NoWorldWritableCheck extends BaseCheck {

    private static final Set<String> FILE_MODULE_KEYS = Set.of("copy", "file", "template", "assemble");

    private static boolean isWorldWritable(Object value) {
        if (value == null) return false;
        int mode;
        if (value instanceof Number n) {
            mode = n.intValue();
        } else if (value instanceof String s) {
            try {
                mode = Integer.parseInt(s, 8);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
        // World writable: o+w (0002) or g+w and o+w (0022), etc.
        return (mode & 0002) != 0;
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
        if (mode != null && isWorldWritable(mode)) {
            addLineIssue(task.line(), "Avoid world-writable file mode (e.g. 0o002, 0o022).");
        }
    }
}
