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
package com.qualimetry.sonar.ansible.plugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SensorPathResolverTest {

    private FileSystem fileSystem;
    private FilePredicates predicates;

    @BeforeEach
    void setUp() {
        fileSystem = mock(FileSystem.class);
        predicates = mock(FilePredicates.class);
        when(fileSystem.predicates()).thenReturn(predicates);
        when(predicates.all()).thenReturn(mock(FilePredicate.class));
    }

    private InputFile mockFile(String relativePath) {
        InputFile f = mock(InputFile.class);
        when(f.relativePath()).thenReturn(relativePath);
        return f;
    }

    private SensorPathResolver resolver(String currentFilePath, List<InputFile> projectFiles) {
        InputFile currentFile = mockFile(currentFilePath);
        when(fileSystem.inputFiles(any())).thenReturn(projectFiles);
        return new SensorPathResolver(fileSystem, currentFile);
    }

    @Test
    void existsInProject_findsFileInSameDirectory() {
        InputFile target = mockFile("roles/tasks/install.yml");
        SensorPathResolver r = resolver("roles/tasks/main.yml", List.of(target));
        assertThat(r.existsInProject("install.yml")).isTrue();
    }

    @Test
    void existsInProject_findsFileWithRelativePath() {
        InputFile target = mockFile("roles/defaults/main.yml");
        SensorPathResolver r = resolver("roles/tasks/main.yml", List.of(target));
        assertThat(r.existsInProject("../defaults/main.yml")).isTrue();
    }

    @Test
    void existsInProject_returnsFalseForMissingFile() {
        SensorPathResolver r = resolver("roles/tasks/main.yml", List.of());
        assertThat(r.existsInProject("nonexistent.yml")).isFalse();
    }

    @Test
    void existsInProject_returnsFalseForNull() {
        SensorPathResolver r = resolver("roles/tasks/main.yml", List.of());
        assertThat(r.existsInProject(null)).isFalse();
    }

    @Test
    void existsInProject_returnsFalseForBlank() {
        SensorPathResolver r = resolver("roles/tasks/main.yml", List.of());
        assertThat(r.existsInProject("   ")).isFalse();
    }

    @Test
    void existsInProject_normalizesBackslashes() {
        InputFile target = mockFile("roles/tasks/install.yml");
        SensorPathResolver r = resolver("roles/tasks/main.yml", List.of(target));
        assertThat(r.existsInProject("install.yml")).isTrue();
    }

    @Test
    void existsInProject_resolvesCurrentDotPath() {
        InputFile target = mockFile("roles/tasks/install.yml");
        SensorPathResolver r = resolver("roles/tasks/main.yml", List.of(target));
        assertThat(r.existsInProject("./install.yml")).isTrue();
    }

    @Test
    void existsInProject_fileAtRoot() {
        InputFile target = mockFile("playbook.yml");
        SensorPathResolver r = resolver("site.yml", List.of(target));
        assertThat(r.existsInProject("playbook.yml")).isTrue();
    }

    @Test
    void existsInProject_deepNestedRelativePath() {
        InputFile target = mockFile("common/vars/defaults.yml");
        SensorPathResolver r = resolver("roles/web/tasks/main.yml", List.of(target));
        assertThat(r.existsInProject("../../common/vars/defaults.yml")).isFalse();
    }

    @Test
    void existsInProject_multipleDotDotResolution() {
        InputFile target = mockFile("handlers/main.yml");
        SensorPathResolver r = resolver("roles/tasks/main.yml", List.of(target));
        assertThat(r.existsInProject("../../handlers/main.yml")).isTrue();
    }
}
