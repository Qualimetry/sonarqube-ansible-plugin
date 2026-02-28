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
 * run_once can lead to unexpected behavior; flag for review.
 */
@Rule(key = "qa-run-once-documented")
public class RunOnceCheck extends BaseCheck {

    @Override
    public void visitTask(Task task) {
        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        Object runOnce = attrs.get("run_once");
        if (runOnce == null) return;
        if (Boolean.TRUE.equals(runOnce) || "true".equals(String.valueOf(runOnce).trim())) {
            addLineIssue(task.line(), "run_once can lead to unexpected behavior; ensure it is intentional.");
        }
    }
}
