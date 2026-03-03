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
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleRef;
import com.qualimetry.sonar.ansible.analyzer.parser.model.Task;

/**
 * Walks a PlaybookFile tree and invokes the visitor callbacks.
 */
public final class AnsibleWalker {

    private AnsibleWalker() {
    }

    public static void walk(PlaybookFile file, AnsibleVisitor visitor) {
        visitor.visitPlaybookFile(file);

        for (Play play : file.plays()) {
            visitor.visitPlay(play);
            for (Task task : play.tasks()) {
                visitor.visitTask(task);
            }
            for (RoleRef roleRef : play.roles()) {
                visitor.visitRoleRef(roleRef);
                visitor.leaveRoleRef(roleRef);
            }
            visitor.leavePlay(play);
        }

        visitor.leavePlaybookFile(file);
    }
}
