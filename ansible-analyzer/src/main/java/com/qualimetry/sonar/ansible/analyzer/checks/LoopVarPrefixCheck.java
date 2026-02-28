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
 * Prefer a prefix for loop_control.loop_var (e.g. item_) to avoid shadowing and clarify scope.
 */
@Rule(key = "qa-prefix-loop-var")
public class LoopVarPrefixCheck extends BaseCheck {

    private static final String RECOMMENDED_PREFIX = "item_";

    @Override
    public void visitTask(Task task) {
        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;

        Object loopControl = attrs.get("loop_control");
        if (!(loopControl instanceof Map<?, ?> lc)) return;

        Object loopVar = lc.get("loop_var");
        if (!(loopVar instanceof String name) || name.isBlank()) return;

        if (name.startsWith(RECOMMENDED_PREFIX)) return;
        if (name.equals("item")) return; // default is acceptable

        addLineIssue(task.line(), "Prefer a prefix for loop_var (e.g. " + RECOMMENDED_PREFIX + "name) to avoid shadowing; current: \"" + name + "\".");
    }
}
