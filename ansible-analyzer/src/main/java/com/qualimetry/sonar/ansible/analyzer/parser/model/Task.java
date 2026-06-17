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
package com.qualimetry.sonar.ansible.analyzer.parser.model;

import java.util.Map;

/**
 * Represents a single Ansible task (e.g. a map with name, module, args).
 *
 * @param name       task name, or null
 * @param moduleKey  FQCN or short module name, or null for raw/include
 * @param line       approximate line in the file (1-based)
 * @param attributes full task map for checks that need it
 */
public record Task(String name, String moduleKey, int line, Map<String, Object> attributes) {
}
