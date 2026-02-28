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

import com.qualimetry.sonar.ansible.analyzer.parser.model.Play;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Plays should have tags so you can run a subset of the playbook (e.g. --tags).
 */
@Rule(key = "qa-play-has-tags")
public class RequiredTagsCheck extends BaseCheck {

    @Override
    public void visitPlay(Play play) {
        if (play.tags() == null || play.tags().isEmpty()) {
            addLineIssue(play.line(), "Play should have tags for selective execution.");
        }
    }
}
