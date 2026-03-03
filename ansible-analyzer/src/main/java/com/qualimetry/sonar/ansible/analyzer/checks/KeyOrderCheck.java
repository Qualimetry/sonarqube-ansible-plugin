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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tasks should list "name" before the module key for readability.
 */
@Rule(key = "qa-task-name-first")
public class KeyOrderCheck extends BaseCheck {

    private static final List<String> META_KEYS = List.of(
            "name", "block", "include_role", "include_tasks", "import_role", "import_tasks",
            "include", "import_playbook", "when", "loop", "tags", "become", "become_user");

    @Override
    public void visitTask(Task task) {
        Map<String, Object> attrs = task.attributes();
        if (attrs == null || attrs.isEmpty()) return;
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;
        // Resolve module key (e.g. "command" from task; include might be "include: path")
        String actionKey = moduleKey.contains(":") ? moduleKey.substring(0, moduleKey.indexOf(':')) : moduleKey;
        if (META_KEYS.contains(actionKey)) return;

        List<String> keys = new ArrayList<>(attrs.keySet());
        int nameIdx = keys.indexOf("name");
        int actionIdx = keys.indexOf(actionKey);
        if (nameIdx >= 0 && actionIdx >= 0 && actionIdx < nameIdx) {
            addLineIssue(task.line(), "Put \"name\" before the module key (\"" + actionKey + "\").");
        }
    }
}
