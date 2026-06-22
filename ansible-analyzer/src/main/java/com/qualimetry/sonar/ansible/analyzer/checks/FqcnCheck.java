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

/**
 * Use fully qualified collection names (FQCN) for modules, e.g. ansible.builtin.copy instead of copy.
 */
@Rule(key = "qa-full-module-name")
public class FqcnCheck extends BaseCheck {

    @Override
    public void visitTask(Task task) {
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;
        // Skip include/import_playbook etc (not module FQCNs)
        if (moduleKey.startsWith("include") || moduleKey.startsWith("import_")) return;
        String actionPart = moduleKey.contains(":") ? moduleKey.substring(0, moduleKey.indexOf(':')) : moduleKey;
        if (actionPart.contains(".")) return; // Already FQCN
        addLineIssue(task.line(), "Use FQCN for modules (e.g. ansible.builtin." + actionPart + " instead of " + actionPart + ").");
    }
}
