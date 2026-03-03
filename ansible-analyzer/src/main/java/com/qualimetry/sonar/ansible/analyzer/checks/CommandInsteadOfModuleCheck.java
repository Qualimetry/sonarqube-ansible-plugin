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
 * Prefer Ansible modules over command when equivalent (e.g. copy instead of command: cp).
 */
@Rule(key = "qa-use-module-not-command")
public class CommandInsteadOfModuleCheck extends BaseCheck {

    private static final List<CommandSuggestion> SUGGESTIONS = List.of(
            new CommandSuggestion("cp ", "copy"),
            new CommandSuggestion("mkdir ", "file with state: directory"),
            new CommandSuggestion("mv ", "copy (remove src) or file"),
            new CommandSuggestion("chmod ", "file with mode:"),
            new CommandSuggestion("chown ", "file with owner:"),
            new CommandSuggestion("ln -s ", "file with state: link"),
            new CommandSuggestion("rm ", "file with state: absent"),
            new CommandSuggestion("touch ", "file with state: touch")
    );

    @Override
    public void visitTask(Task task) {
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;
        if (!"command".equals(moduleKey)) return;

        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;

        String cmd = getCommandString(attrs);
        if (cmd == null || cmd.isBlank()) return;

        String normalized = cmd.stripLeading().toLowerCase();
        for (CommandSuggestion s : SUGGESTIONS) {
            if (normalized.startsWith(s.prefix)) {
                addLineIssue(task.line(), "Prefer the " + s.suggestion + " module instead of command: " + s.prefix.trim() + " ....");
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static String getCommandString(Map<String, Object> attrs) {
        Object cmdObj = attrs.get("cmd");
        if (cmdObj instanceof String s) return s;
        Object argv = attrs.get("argv");
        if (argv instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof String s) return s;
        Object cmdMap = attrs.get("command");
        if (cmdMap instanceof Map<?, ?> map) {
            Object c = map.get("cmd");
            if (c instanceof String s) return s;
        }
        return null;
    }

    private record CommandSuggestion(String prefix, String suggestion) {}
}
