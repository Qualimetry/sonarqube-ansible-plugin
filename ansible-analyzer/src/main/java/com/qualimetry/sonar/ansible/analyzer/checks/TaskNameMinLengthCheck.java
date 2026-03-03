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
 * Task names should be at least a minimum length for clarity (default 3 characters).
 */
@Rule(key = "qa-task-name-min-chars")
public class TaskNameMinLengthCheck extends BaseCheck {

    private static final int MIN_LENGTH = 3;

    @Override
    public void visitTask(Task task) {
        String name = task.name();
        if (name == null) return;
        if (name.length() < MIN_LENGTH) {
            addLineIssue(task.line(), "Task name should be at least " + MIN_LENGTH + " characters.");
        }
    }
}
