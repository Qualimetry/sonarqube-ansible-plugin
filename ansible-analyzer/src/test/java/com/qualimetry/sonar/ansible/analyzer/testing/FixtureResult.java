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

import java.util.List;

/**
 * Result of running a single check against a single fixture file.
 * Used by EvidenceReportGenerator to build the evidence report.
 */
public record FixtureResult(
        String fixturePath,
        String fixtureContent,
        List<TestIssue> expectedIssues,
        List<TestIssue> actualIssues,
        String status,
        List<String> mismatches) {

    public static final String STATUS_PASS = "PASS";
    public static final String STATUS_FAIL = "FAIL";
    public static final String STATUS_FILE_LEVEL_CHECK = "FILE_LEVEL_CHECK";

    public boolean passed() {
        return STATUS_PASS.equals(status);
    }

    public boolean acceptable() {
        return !STATUS_FAIL.equals(status);
    }

    public String fixtureName() {
        int lastSlash = fixturePath.lastIndexOf('/');
        return lastSlash >= 0 ? fixturePath.substring(lastSlash + 1) : fixturePath;
    }

    /**
     * Returns a new FixtureResult with the status reclassified.
     */
    public FixtureResult withStatus(String newStatus) {
        return new FixtureResult(fixturePath, fixtureContent, expectedIssues,
                actualIssues, newStatus, mismatches);
    }
}
