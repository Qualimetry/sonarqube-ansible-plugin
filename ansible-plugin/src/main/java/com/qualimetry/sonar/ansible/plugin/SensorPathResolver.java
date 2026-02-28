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

import com.qualimetry.sonar.ansible.analyzer.visitor.PathResolver;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolves paths relative to the current file using Sonar FileSystem.
 */
public class SensorPathResolver implements PathResolver {

    private final FileSystem fileSystem;
    private final String currentFileRelativePath;

    public SensorPathResolver(FileSystem fileSystem, InputFile currentFile) {
        this.fileSystem = fileSystem;
        this.currentFileRelativePath = currentFile.relativePath();
    }

    @Override
    public boolean existsInProject(String pathRelativeToCurrentFile) {
        if (pathRelativeToCurrentFile == null || pathRelativeToCurrentFile.isBlank()) {
            return false;
        }
        String resolved = resolveRelative(pathRelativeToCurrentFile);
        if (resolved == null) return false;
        for (InputFile f : fileSystem.inputFiles(fileSystem.predicates().all())) {
            if (resolved.equals(f.relativePath())) {
                return true;
            }
        }
        return false;
    }

    private String resolveRelative(String pathRelativeToCurrentFile) {
        String baseDir = baseDirOf(currentFileRelativePath);
        String combined = baseDir.isEmpty()
                ? pathRelativeToCurrentFile.replace('\\', '/')
                : baseDir + "/" + pathRelativeToCurrentFile.replace('\\', '/');
        return normalizePath(combined);
    }

    private static String baseDirOf(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) return "";
        int last = relativePath.lastIndexOf('/');
        if (last <= 0) return "";
        return relativePath.substring(0, last);
    }

    private static String normalizePath(String path) {
        if (path == null || path.isEmpty()) return path;
        Path p = Paths.get(path);
        List<String> parts = new ArrayList<>();
        for (Path name : p) {
            String s = name.toString();
            if (".".equals(s)) continue;
            if ("..".equals(s)) {
                if (!parts.isEmpty()) parts.remove(parts.size() - 1);
                continue;
            }
            parts.add(s);
        }
        return String.join("/", parts);
    }
}
