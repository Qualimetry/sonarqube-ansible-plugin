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

import com.qualimetry.sonar.ansible.analyzer.parser.model.ParseError;
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleMeta;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses role meta/main.yml into RoleMeta (galaxy_info, dependencies, etc.).
 */
public class RoleMetaParser {

    /**
     * Parses meta/main.yml content. On YAML failure returns RoleMeta with parseError set.
     */
    public RoleMeta parse(String uri, String content) {
        if (content == null || content.isBlank()) {
            return RoleMeta.empty(uri);
        }
        try {
            Object root = new Yaml().load(content);
            if (root == null) {
                return RoleMeta.empty(uri);
            }
            return buildRoleMeta(uri, root);
        } catch (YAMLException e) {
            int line = 0;
            if (e instanceof MarkedYAMLException marked && marked.getProblemMark() != null) {
                line = marked.getProblemMark().getLine() + 1;
            }
            String message = e.getMessage() != null ? e.getMessage() : "YAML parse error";
            return RoleMeta.withParseError(uri, new ParseError(message, line));
        }
    }

    private static RoleMeta buildRoleMeta(String uri, Object root) {
        if (!(root instanceof Map<?, ?> rootMap)) {
            return RoleMeta.empty(uri);
        }
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<?, ?> e : rootMap.entrySet()) {
            if (e.getKey() != null) {
                map.put(e.getKey().toString(), e.getValue());
            }
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> galaxyInfo = map.get("galaxy_info") instanceof Map
                ? (Map<String, Object>) map.get("galaxy_info")
                : Map.of();
        List<Object> dependencies = toList(map.get("dependencies"));
        List<String> galaxyTags = toStringList(galaxyInfo.get("galaxy_tags"));
        List<String> videoLinks = toStringList(galaxyInfo.get("video_links"));
        Boolean allowDuplicates = toBoolean(map.get("allow_duplicates"));

        return new RoleMeta(
                galaxyInfo,
                dependencies,
                galaxyTags,
                videoLinks,
                allowDuplicates,
                uri,
                null);
    }

    private static List<Object> toList(Object o) {
        if (o instanceof List<?> list) {
            return new ArrayList<>(list);
        }
        return List.of();
    }

    private static List<String> toStringList(Object o) {
        if (o == null) return List.of();
        if (o instanceof List<?> list) {
            List<String> out = new ArrayList<>();
            for (Object item : list) {
                if (item != null) out.add(item.toString());
            }
            return out;
        }
        return List.of();
    }

    private static Boolean toBoolean(Object o) {
        if (o == null) return null;
        if (o instanceof Boolean b) return b;
        if (o instanceof String s) return Boolean.parseBoolean(s);
        return null;
    }
}
