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
 * Variable names should be lowercase with letters, numbers, and underscores only.
 */
@Rule(key = "qa-variable-name-format")
public class VarNamingCheck extends BaseCheck {

    private static final Pattern VALID_VAR_NAME = Pattern.compile("^[a-z][a-z0-9_]*$");

    @Override
    public void visitTask(Task task) {
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;
        String base = moduleKey.contains(".") ? moduleKey.substring(moduleKey.lastIndexOf('.') + 1) : moduleKey;
        if (!"set_fact".equals(base)) return;

        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        Object setFactVal = attrs.get(moduleKey);
        if (!(setFactVal instanceof Map<?, ?> facts)) return;
        for (Object k : facts.keySet()) {
            if (k instanceof String name && !VALID_VAR_NAME.matcher(name).matches()) {
                addLineIssue(task.line(), "Variable name should be lowercase with only letters, numbers, and underscores: " + name);
                return;
            }
        }
    }
}
