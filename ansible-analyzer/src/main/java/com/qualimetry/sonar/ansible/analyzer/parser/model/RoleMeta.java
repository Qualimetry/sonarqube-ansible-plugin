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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Parsed structure of a role meta/main.yml (galaxy_info, dependencies, etc.).
 *
 * @param galaxyInfo     galaxy_info map (author, description, galaxy_tags, video_links, etc.)
 * @param dependencies   list of role dependencies (string or map)
 * @param galaxyTags     galaxy_info.galaxy_tags (convenience)
 * @param videoLinks     galaxy_info.video_links (convenience)
 * @param allowDuplicates top-level allow_duplicates
 * @param uri            file URI or path
 * @param parseError     set if YAML parse failed
 */
public record RoleMeta(
        Map<String, Object> galaxyInfo,
        List<Object> dependencies,
        List<String> galaxyTags,
        List<String> videoLinks,
        Boolean allowDuplicates,
        String uri,
        ParseError parseError) {

    public RoleMeta {
        galaxyInfo = galaxyInfo != null ? Map.copyOf(galaxyInfo) : Map.of();
        dependencies = dependencies != null ? List.copyOf(dependencies) : List.of();
        galaxyTags = galaxyTags != null ? List.copyOf(galaxyTags) : List.of();
        videoLinks = videoLinks != null ? List.copyOf(videoLinks) : List.of();
        Objects.requireNonNull(uri, "uri must not be null");
    }

    public static RoleMeta empty(String uri) {
        return new RoleMeta(Map.of(), List.of(), List.of(), List.of(), null, uri, null);
    }

    public static RoleMeta withParseError(String uri, ParseError parseError) {
        return new RoleMeta(Map.of(), List.of(), List.of(), List.of(), null, uri, parseError);
    }
}
