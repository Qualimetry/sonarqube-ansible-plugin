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

/**
 * Detects whether a file path is a role meta/main.yml (e.g. roles/foo/meta/main.yml or meta/main.yml).
 */
public final class RoleMetaDetector {

    private RoleMetaDetector() {
    }

    /**
     * Returns true if the path corresponds to a role meta file (meta/main.yml or meta/main.yaml).
     * Accepts relative path or URI; normalizes slashes.
     */
    public static boolean isRoleMetaFile(String pathOrUri) {
        if (pathOrUri == null || pathOrUri.isBlank()) return false;
        String normalized = pathOrUri.replace('\\', '/').replaceAll("/+", "/");
        return normalized.endsWith("meta/main.yml") || normalized.endsWith("meta/main.yaml");
    }
}
