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
 * Tasks with many attributes or complex when/loop may be hard to maintain.
 */
@Rule(key = "qa-limit-task-attributes")
public class ComplexityCheck extends BaseCheck {

    private static final int MAX_ATTRS = 15;

    @Override
    public void visitTask(Task task) {
        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        int count = countKeys(attrs);
        if (count > MAX_ATTRS) {
            addLineIssue(task.line(), "Task has " + count + " attributes; consider splitting or simplifying (max " + MAX_ATTRS + ").");
        }
    }

    @SuppressWarnings("unchecked")
    private static int countKeys(Map<String, Object> map) {
        int n = map.size();
        for (Object v : map.values()) {
            if (v instanceof Map<?, ?> m) n += countKeys((Map<String, Object>) m);
        }
        return n;
    }
}
