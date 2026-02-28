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

import com.qualimetry.sonar.ansible.analyzer.parser.model.Play;
import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleMeta;
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleRef;
import com.qualimetry.sonar.ansible.analyzer.parser.model.Task;

/**
 * Visitor interface for traversing the Ansible playbook tree.
 */
public interface AnsibleVisitor {

    default void visitPlaybookFile(PlaybookFile file) {
    }

    default void visitRoleMeta(RoleMeta meta) {
    }

    default void visitPlay(Play play) {
    }

    default void visitTask(Task task) {
    }

    default void visitRoleRef(RoleRef roleRef) {
    }

    default void leavePlaybookFile(PlaybookFile file) {
    }

    default void leavePlay(Play play) {
    }

    default void leaveRoleRef(RoleRef roleRef) {
    }
}
