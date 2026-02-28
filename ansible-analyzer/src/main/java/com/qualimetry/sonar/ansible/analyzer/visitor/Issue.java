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
package com.qualimetry.sonar.ansible.analyzer.visitor;

import com.qualimetry.sonar.ansible.analyzer.parser.model.TextPosition;

/**
 * An issue reported by a check.
 *
 * @param ruleKey   rule key
 * @param message   message
 * @param position  optional position
 * @param line      optional line (1-based)
 * @param cost      optional remediation cost
 * @param endColumn optional end column
 */
public record Issue(
        String ruleKey,
        String message,
        TextPosition position,
        Integer line,
        Double cost,
        Integer endColumn) {
}
