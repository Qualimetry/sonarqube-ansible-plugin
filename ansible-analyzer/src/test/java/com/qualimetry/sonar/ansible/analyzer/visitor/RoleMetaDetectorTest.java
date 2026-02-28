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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleMetaDetectorTest {

    @Test
    void whenPathEndsWithMetaMainYml_returnsTrue() {
        assertThat(RoleMetaDetector.isRoleMetaFile("roles/foo/meta/main.yml")).isTrue();
        assertThat(RoleMetaDetector.isRoleMetaFile("meta/main.yml")).isTrue();
        assertThat(RoleMetaDetector.isRoleMetaFile("meta/main.yaml")).isTrue();
    }

    @Test
    void whenPathDoesNotEndWithMetaMain_returnsFalse() {
        assertThat(RoleMetaDetector.isRoleMetaFile("play.yml")).isFalse();
        assertThat(RoleMetaDetector.isRoleMetaFile("meta/other.yml")).isFalse();
        assertThat(RoleMetaDetector.isRoleMetaFile("")).isFalse();
        assertThat(RoleMetaDetector.isRoleMetaFile(null)).isFalse();
    }
}
