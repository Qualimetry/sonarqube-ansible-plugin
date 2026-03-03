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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnsibleWalkerTest {

    @Test
    void walk_invokesVisitorForEveryPlayTaskAndRoleRef() {
        List<String> visited = new ArrayList<>();
        AnsibleVisitor counter = new AnsibleVisitor() {
            @Override
            public void visitPlaybookFile(PlaybookFile file) {
                visited.add("playbookFile");
            }

            @Override
            public void visitPlay(Play play) {
                visited.add("play:" + play.name());
            }

            @Override
            public void visitTask(Task task) {
                visited.add("task:" + (task.name() != null ? task.name() : task.moduleKey()));
            }

            @Override
            public void visitRoleRef(RoleRef roleRef) {
                visited.add("role:" + roleRef.roleName());
            }

            @Override
            public void leavePlaybookFile(PlaybookFile file) {
                visited.add("leavePlaybookFile");
            }

            @Override
            public void leavePlay(Play play) {
                visited.add("leavePlay:" + play.name());
            }

            @Override
            public void leaveRoleRef(RoleRef roleRef) {
                visited.add("leaveRoleRef:" + roleRef.roleName());
            }
        };

        PlaybookFile file = new PlaybookFile(List.of(
                new Play("P1", List.of(
                        new Task("T1", "ping", 5, java.util.Map.of()),
                        new Task("T2", "debug", 7, java.util.Map.of())
                ), List.of(new RoleRef("common", 3)), 2, List.of())
        ), "file:///test.yml");

        AnsibleWalker.walk(file, counter);

        assertThat(visited).containsExactly(
                "playbookFile",
                "play:P1",
                "task:T1",
                "task:T2",
                "role:common",
                "leaveRoleRef:common",
                "leavePlay:P1",
                "leavePlaybookFile"
        );
    }
}
