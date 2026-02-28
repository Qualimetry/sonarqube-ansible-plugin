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
 * Avoid free-form for command/shell: use the args form (e.g. cmd: ...) instead of a bare string.
 */
@Rule(key = "qa-command-args-form")
public class NoFreeFormCheck extends BaseCheck {

    @Override
    public void visitTask(Task task) {
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;
        String actionKey = moduleKey.contains(":") ? moduleKey.substring(0, moduleKey.indexOf(':')) : moduleKey;
        if (!"command".equals(actionKey) && !"shell".equals(actionKey)) return;

        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        Object cmdVal = attrs.get(actionKey);
        if (cmdVal instanceof String) {
            addLineIssue(task.line(), "Use the args form for " + actionKey + " (e.g. cmd: \"...\") instead of free-form.");
        }
    }
}
