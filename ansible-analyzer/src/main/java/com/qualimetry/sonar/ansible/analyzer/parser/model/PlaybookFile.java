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

import java.util.List;
import java.util.Objects;

/**
 * Root of the parsed Ansible playbook tree.
 *
 * @param plays     list of plays (may be empty if parse failed or file is not a playbook)
 * @param uri       file URI or path
 * @param parseError if non-null, YAML parsing failed; plays is empty and checks should report this
 */
public record PlaybookFile(List<Play> plays, String uri, ParseError parseError) {

    public PlaybookFile {
        Objects.requireNonNull(plays, "plays must not be null");
        plays = List.copyOf(plays);
        Objects.requireNonNull(uri, "uri must not be null");
    }

    /**
     * Creates a successful parse result (no parse error).
     */
    public PlaybookFile(List<Play> plays, String uri) {
        this(plays, uri, null);
    }
}
