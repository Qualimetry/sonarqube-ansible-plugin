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
import java.util.regex.Pattern;

/**
 * Prefer command module over shell when the command does not use shell features (pipes, redirects, etc.).
 */
@Rule(key = "qa-command-not-shell-when-possible")
public class CommandInsteadOfShellCheck extends BaseCheck {

    private static final Pattern SHELL_METACHAR = Pattern.compile("[|&;<>$()`\\\\]");

    @Override
    public void visitTask(Task task) {
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;
        if (!"shell".equals(moduleKey)) return;

        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;

        String cmd = getShellCommandString(attrs);
        if (cmd == null || cmd.isBlank()) return;

        if (SHELL_METACHAR.matcher(cmd).find()) return; // Uses shell features, shell is appropriate

        addLineIssue(task.line(), "Use the \"command\" module instead of \"shell\" when you do not need shell features (pipes, redirects, etc.).");
    }

    @SuppressWarnings("unchecked")
    private static String getShellCommandString(Map<String, Object> attrs) {
        Object shellVal = attrs.get("shell");
        if (shellVal instanceof String s) return s;
        if (shellVal instanceof Map<?, ?> map) {
            Object c = map.get("cmd");
            if (c instanceof String s) return s;
            Object argv = map.get("argv");
            if (argv instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof String s) return s;
        }
        Object cmdObj = attrs.get("cmd");
        if (cmdObj instanceof String s) return s;
        return null;
    }
}
