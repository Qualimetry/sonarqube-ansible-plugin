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
 * Resolves paths relative to the current file and checks if the target exists in the project.
 * Supplied by the Sensor (using Sonar FileSystem and current InputFile) when available.
 */
public interface PathResolver {

    /**
     * Returns true if the given path (relative to the current file's directory) exists
     * in the project as a main or test file.
     *
     * @param pathRelativeToCurrentFile path as used in include_tasks, import_tasks, import_playbook
     * @return true if the resolved file exists in the project
     */
    boolean existsInProject(String pathRelativeToCurrentFile);
}
