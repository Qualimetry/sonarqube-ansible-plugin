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
import java.util.regex.Pattern;

/**
 * Avoid plain HTTP URLs in get_url, uri, or similar; use HTTPS when possible.
 */
@Rule(key = "qa-require-https")
public class NoHttpWithoutTlsCheck extends BaseCheck {

    private static final Pattern HTTP_URL = Pattern.compile("https?://[^\\s'\"]+", Pattern.CASE_INSENSITIVE);

    private static boolean hasPlainHttp(String s) {
        if (s == null) return false;
        var matcher = HTTP_URL.matcher(s);
        while (matcher.find()) {
            String url = matcher.group();
            if (url.toLowerCase().startsWith("http://")) return true;
        }
        return false;
    }

    private static boolean checkValue(Object value) {
        if (value == null) return false;
        if (value instanceof String s) return hasPlainHttp(s);
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitTask(Task task) {
        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        if (checkValue(attrs.get("url")) || checkValue(attrs.get("dest"))) {
            addLineIssue(task.line(), "Use HTTPS instead of HTTP for URLs.");
            return;
        }
        for (String key : new String[]{"get_url", "uri"}) {
            Object args = attrs.get(key);
            if (args instanceof Map<?, ?> m) {
                if (checkValue(m.get("url"))) {
                    addLineIssue(task.line(), "Use HTTPS instead of HTTP for URLs.");
                    return;
                }
            }
        }
    }
}
