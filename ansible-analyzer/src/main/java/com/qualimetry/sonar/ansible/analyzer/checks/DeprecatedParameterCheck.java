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
 * Flag use of deprecated module parameters. Prefer the recommended alternatives.
 */
@Rule(key = "qa-replace-deprecated-param")
public class DeprecatedParameterCheck extends BaseCheck {

    /** Map of parameter name -> suggested replacement (module-agnostic or common). */
    private static final Map<String, String> DEPRECATED_PARAMS = Map.of(
            "force", "Use state (e.g. state: present/absent) instead of force."
    );

    @Override
    public void visitTask(Task task) {
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;

        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;

        // Module args are nested under the module key (e.g. copy: { force: yes })
        Object moduleArgs = attrs.get(moduleKey);
        if (!(moduleArgs instanceof Map<?, ?> args)) return;

        for (String param : DEPRECATED_PARAMS.keySet()) {
            if (args.containsKey(param)) {
                addLineIssue(task.line(), DEPRECATED_PARAMS.get(param));
                return;
            }
        }
    }
}
