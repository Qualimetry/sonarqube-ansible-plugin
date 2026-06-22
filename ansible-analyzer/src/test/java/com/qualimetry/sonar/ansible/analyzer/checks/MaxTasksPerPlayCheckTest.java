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

import com.qualimetry.sonar.ansible.analyzer.parser.AnsibleParser;
import com.qualimetry.sonar.ansible.analyzer.parser.model.Play;
import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.parser.model.Task;
import com.qualimetry.sonar.ansible.analyzer.visitor.AnsibleContext;
import com.qualimetry.sonar.ansible.analyzer.visitor.AnsibleWalker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MaxTasksPerPlayCheckTest {

    private static final String URI = "file:///repo/play.yml";
    private final AnsibleParser parser = new AnsibleParser();

    private AnsibleContext runCheck(PlaybookFile file) {
        AnsibleContext context = new AnsibleContext(file);
        MaxTasksPerPlayCheck check = new MaxTasksPerPlayCheck();
        check.setContext(context);
        AnsibleWalker.walk(file, check);
        return context;
    }

    @Test
    void whenPlayHasFewTasks_reportsNoIssue() {
        String yaml = """
            - hosts: all
              tasks:
                - name: One
                  ping:
                - name: Two
                  debug:
            """;
        PlaybookFile file = parser.parse(URI, yaml);
        assertThat(runCheck(file).getIssues()).isEmpty();
    }

    @Test
    void whenPlayHasMoreThan50Tasks_reportsIssue() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 51; i++) {
            tasks.add(new Task("T" + i, "debug", i + 2, Map.of()));
        }
        Play play = new Play("Big", tasks, List.of(), 1, List.of());
        PlaybookFile file = new PlaybookFile(List.of(play), URI);
        AnsibleContext context = runCheck(file);
        assertThat(context.getIssues()).hasSize(1);
        assertThat(context.getIssues().get(0).ruleKey()).isEqualTo("qa-limit-tasks-per-play");
        assertThat(context.getIssues().get(0).message()).contains("51");
    }
}
