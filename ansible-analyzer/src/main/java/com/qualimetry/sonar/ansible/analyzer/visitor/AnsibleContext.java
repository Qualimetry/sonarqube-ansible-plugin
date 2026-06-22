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

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import org.sonar.api.batch.fs.InputFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Context for a single playbook file analysis (parsed tree, optional InputFile, raw content, issues).
 */
public class AnsibleContext {

    private final PlaybookFile playbookFile;
    private final InputFile inputFile;
    private final String rawContent;
    private final List<Issue> issues;
    private PathResolver pathResolver;

    public AnsibleContext(PlaybookFile playbookFile, InputFile inputFile, String rawContent) {
        this.playbookFile = Objects.requireNonNull(playbookFile);
        this.inputFile = inputFile;
        this.rawContent = rawContent;
        this.issues = new ArrayList<>();
    }

    public AnsibleContext(PlaybookFile playbookFile) {
        this(playbookFile, null, null);
    }

    public PlaybookFile getPlaybookFile() {
        return playbookFile;
    }

    public InputFile getInputFile() {
        return inputFile;
    }

    public String getRawContent() {
        return rawContent;
    }

    public PathResolver getPathResolver() {
        return pathResolver;
    }

    public void setPathResolver(PathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }

    public void addIssue(Issue issue) {
        issues.add(Objects.requireNonNull(issue));
    }

    public List<Issue> getIssues() {
        return Collections.unmodifiableList(issues);
    }
}
