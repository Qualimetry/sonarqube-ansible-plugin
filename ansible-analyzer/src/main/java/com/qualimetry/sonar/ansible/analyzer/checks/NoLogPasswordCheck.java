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
 * Tasks that handle passwords or secrets should set no_log: true to avoid logging.
 */
@Rule(key = "qa-no-log-secrets")
public class NoLogPasswordCheck extends BaseCheck {

    private static boolean isNoLogTrue(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean b) return b;
        if (value instanceof String s) return "yes".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s);
        return false;
    }

    private static boolean keySuggestsSecret(String key) {
        if (key == null) return false;
        String lower = key.toLowerCase();
        return lower.contains("password") || lower.contains("secret") || lower.equals("token");
    }

    @SuppressWarnings("unchecked")
    private static boolean hasSecretKeyIn(Map<String, Object> attrs) {
        if (attrs == null) return false;
        for (String key : attrs.keySet()) {
            if (keySuggestsSecret(key)) return true;
            Object val = attrs.get(key);
            if (val instanceof Map) {
                if (hasSecretKeyIn((Map<String, Object>) val)) return true;
            }
        }
        return false;
    }

    @Override
    public void visitTask(Task task) {
        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        if (!hasSecretKeyIn(attrs)) return;
        if (!isNoLogTrue(attrs.get("no_log"))) {
            addLineIssue(task.line(), "Set \"no_log: true\" when the task handles passwords or secrets.");
        }
    }
}
