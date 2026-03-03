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

import com.qualimetry.sonar.ansible.analyzer.parser.model.Play;
import com.qualimetry.sonar.ansible.analyzer.parser.model.Task;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.HashSet;
import java.util.Set;

/**
 * Avoid duplicate tasks (same name and module) within a play; they may indicate copy-paste or redundant logic.
 */
@Rule(key = "qa-unique-tasks")
public class NoDuplicateTasksCheck extends BaseCheck {

    @Override
    public void leavePlay(Play play) {
        Set<String> seen = new HashSet<>();
        for (Task task : play.tasks()) {
            String key = keyOf(task);
            if (!seen.add(key)) {
                addLineIssue(task.line(), "Duplicate task (same name and module); remove or consolidate.");
            }
        }
    }

    private static String keyOf(Task task) {
        String name = task.name() != null ? task.name() : "";
        String module = task.moduleKey() != null ? task.moduleKey() : "";
        return name + "|" + module;
    }
}
