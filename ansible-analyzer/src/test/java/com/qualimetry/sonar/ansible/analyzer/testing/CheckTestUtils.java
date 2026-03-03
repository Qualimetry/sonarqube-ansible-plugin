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
package com.qualimetry.sonar.ansible.analyzer.testing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utilities for rule check tests and evidence report generation.
 */
public final class CheckTestUtils {

    private static final String DESCRIPTION_RESOURCE_BASE =
            "/com/qualimetry/sonar/ansible/analyzer/checks/";

    private CheckTestUtils() {}

    public static String fixturePathForRule(String ruleKey, String fileName) {
        return "checks/" + ruleKey + "/" + fileName;
    }

    /**
     * Discovers all .yml and .yaml fixture files for a rule under checks/&lt;ruleKey&gt;/.
     */
    public static List<String> discoverFixturesForRule(String ruleKey) {
        try {
            java.net.URL dirUrl = CheckTestUtils.class.getResource("/checks/" + ruleKey);
            if (dirUrl == null) {
                return Collections.emptyList();
            }
            Path dir = Path.of(dirUrl.toURI());
            try (Stream<Path> files = java.nio.file.Files.list(dir)) {
                return files
                        .filter(p -> {
                            String name = p.getFileName().toString().toLowerCase();
                            return name.endsWith(".yml") || name.endsWith(".yaml");
                        })
                        .sorted()
                        .map(p -> "checks/" + ruleKey + "/" + p.getFileName().toString())
                        .toList();
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static String readRuleDescriptionHtml(String ruleKey) {
        String resourcePath = DESCRIPTION_RESOURCE_BASE + ruleKey + ".html";
        try (InputStream is = CheckTestUtils.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String toDisplayName(String ruleKey) {
        if (ruleKey == null || ruleKey.isEmpty()) {
            return ruleKey;
        }
        String[] parts = ruleKey.split("-");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            String part = parts[i];
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    sb.append(part.substring(1));
                }
            }
        }
        return sb.toString();
    }
}
