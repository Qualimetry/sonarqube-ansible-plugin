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
package com.qualimetry.sonar.ansible.analyzer.parser;

import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleMeta;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleMetaParserTest {

    private static final String URI = "file:///repo/roles/foo/meta/main.yml";
    private final RoleMetaParser parser = new RoleMetaParser();

    @Test
    void whenValidMeta_parsesGalaxyInfoAndDependencies() {
        String yaml = """
            galaxy_info:
              author: me
              galaxy_tags: [tag1, tag2]
              video_links: [https://example.com/v]
            dependencies:
              - some.role
              - role: other.role
            allow_duplicates: false
            """;
        RoleMeta meta = parser.parse(URI, yaml);
        assertThat(meta.parseError()).isNull();
        assertThat(meta.galaxyInfo()).containsKey("author");
        assertThat(meta.galaxyTags()).containsExactly("tag1", "tag2");
        assertThat(meta.videoLinks()).containsExactly("https://example.com/v");
        assertThat(meta.dependencies()).hasSize(2);
        assertThat(meta.allowDuplicates()).isFalse();
    }

    @Test
    void whenEmptyContent_returnsEmptyMeta() {
        RoleMeta meta = parser.parse(URI, "");
        assertThat(meta.parseError()).isNull();
        assertThat(meta.galaxyInfo()).isEmpty();
        assertThat(meta.dependencies()).isEmpty();
    }

    @Test
    void whenInvalidYaml_returnsParseError() {
        RoleMeta meta = parser.parse(URI, "invalid: [unclosed");
        assertThat(meta.parseError()).isNotNull();
        assertThat(meta.parseError().message()).isNotEmpty();
    }
}
