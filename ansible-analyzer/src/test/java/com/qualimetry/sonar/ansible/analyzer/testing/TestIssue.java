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

/**
 * Represents an expected issue from a "# Noncompliant" annotation in a fixture.
 * Used by CheckVerifier to compare expected vs actual issues.
 *
 * @param line        the 1-based line number where the issue is expected
 * @param message     the expected issue message, or null if only line is asserted
 * @param startColumn the expected start column (1-based), or null
 * @param endColumn   the expected end column (1-based), or null
 */
public record TestIssue(
        int line,
        String message,
        Integer startColumn,
        Integer endColumn) {

    public TestIssue(int line) {
        this(line, null, null, null);
    }

    public TestIssue(int line, String message) {
        this(line, message, null, null);
    }
}
