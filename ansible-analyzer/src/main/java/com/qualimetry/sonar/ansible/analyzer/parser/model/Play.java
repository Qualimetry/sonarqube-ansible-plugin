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
 * Represents a single play in a playbook (hosts, name, tasks, roles, etc.).
 *
 * @param name  play name, or null
 * @param tasks list of tasks in this play (flattened from tasks, pre_tasks, post_tasks, blocks)
 * @param roles list of role references
 * @param line  approximate starting line (1-based)
 * @param tags  play-level tags (may be empty)
 */
public record Play(String name, List<Task> tasks, List<RoleRef> roles, int line, List<String> tags) {

    public Play {
        Objects.requireNonNull(tasks, "tasks must not be null");
        tasks = List.copyOf(tasks);
        Objects.requireNonNull(roles, "roles must not be null");
        roles = List.copyOf(roles);
        Objects.requireNonNull(tags, "tags must not be null");
        tags = List.copyOf(tags);
    }
}
