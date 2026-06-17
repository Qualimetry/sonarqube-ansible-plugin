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

import java.util.List;
import java.util.Map;

/**
 * Shell tasks that use pipes can be fragile (set -o pipefail, error handling). Flag for review.
 */
@Rule(key = "qa-shell-pipe-safe")
public class RiskyShellPipeCheck extends BaseCheck {

    @Override
    public void visitTask(Task task) {
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;
        if (!"shell".equals(moduleKey)) return;

        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;

        String cmd = getShellCommandString(attrs);
        if (cmd == null || !cmd.contains("|")) return;

        addLineIssue(task.line(), "Shell commands with pipes may fail silently; consider set -o pipefail or splitting into separate tasks.");
    }

    @SuppressWarnings("unchecked")
    private static String getShellCommandString(Map<String, Object> attrs) {
        Object shellVal = attrs.get("shell");
        if (shellVal instanceof String s) return s;
        if (shellVal instanceof Map<?, ?> map) {
            Object c = map.get("cmd");
            if (c instanceof String s) return s;
        }
        Object cmdObj = attrs.get("cmd");
        if (cmdObj instanceof String s) return s;
        Object argv = attrs.get("argv");
        if (argv instanceof List<?> list && !list.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Object o : list) {
                if (o instanceof String s) sb.append(s).append(" ");
            }
            return sb.toString();
        }
        return null;
    }
}
