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

import java.util.Set;

/**
 * Flag use of deprecated Ansible modules. Prefer the recommended alternatives.
 */
@Rule(key = "qa-replace-deprecated-module")
public class DeprecatedModuleCheck extends BaseCheck {

    /** Short or FQCN module names that are deprecated (with suggested replacement). */
    private static final Set<String> DEPRECATED = Set.of(
            "include"  // use import_playbook or include_tasks
    );

    @Override
    public void visitTask(Task task) {
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;
        String actionPart = moduleKey.contains(":") ? moduleKey.substring(0, moduleKey.indexOf(':')) : moduleKey;
        String baseName = actionPart.contains(".") ? actionPart.substring(actionPart.lastIndexOf('.') + 1) : actionPart;
        if (!DEPRECATED.contains(baseName)) return;
        String suggestion = "include".equals(baseName)
                ? "Use import_playbook or include_tasks instead of include."
                : "This module is deprecated; use the recommended alternative.";
        addLineIssue(task.line(), suggestion);
    }
}
