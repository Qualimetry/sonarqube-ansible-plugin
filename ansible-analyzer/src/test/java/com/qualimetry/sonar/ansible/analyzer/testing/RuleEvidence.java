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
 * Aggregated evidence for a single rule: metadata and fixture results.
 */
public record RuleEvidence(
        String ruleKey,
        String displayName,
        String severity,
        boolean defaultActive,
        String descriptionHtml,
        List<FixtureResult> fixtureResults) {

    public boolean allAcceptable() {
        return fixtureResults.stream().allMatch(FixtureResult::acceptable);
    }

    public long passedCount() {
        return fixtureResults.stream().filter(FixtureResult::passed).count();
    }

    public long failedCount() {
        return fixtureResults.stream()
                .filter(f -> FixtureResult.STATUS_FAIL.equals(f.status()))
                .count();
    }

    public int fixtureCount() {
        return fixtureResults.size();
    }
}
