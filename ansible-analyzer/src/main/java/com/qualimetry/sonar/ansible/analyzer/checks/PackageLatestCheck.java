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
 * Avoid state: latest in package modules; pin versions for reproducibility.
 */
@Rule(key = "qa-pin-package-version")
public class PackageLatestCheck extends BaseCheck {

    private static final Set<String> PACKAGE_MODULES = Set.of("apt", "yum", "dnf", "package");

    @Override
    public void visitTask(Task task) {
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;
        String action = moduleKey.contains(".") ? moduleKey.substring(moduleKey.lastIndexOf('.') + 1) : moduleKey;
        if (!PACKAGE_MODULES.contains(action)) return;

        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        Object moduleArgs = attrs.get(moduleKey);
        if (moduleArgs == null) moduleArgs = attrs.get(action);
        if (!(moduleArgs instanceof Map<?, ?> args)) return;
        Object state = args.get("state");
        if ("latest".equals(String.valueOf(state).trim())) {
            addLineIssue(task.line(), "Avoid state: latest; pin package versions for reproducibility.");
        }
    }
}
