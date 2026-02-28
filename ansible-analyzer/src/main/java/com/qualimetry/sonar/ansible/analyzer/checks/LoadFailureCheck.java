/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.Task;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import com.qualimetry.sonar.ansible.analyzer.visitor.PathResolver;
import org.sonar.check.Rule;

import java.util.List;

/**
 * Reports when include_tasks, import_tasks, or import_playbook references a path that does not exist in the project.
 */
@Rule(key = "qa-includes-resolve")
public class LoadFailureCheck extends BaseCheck {

    private static final List<String> INCLUDE_KEYS = List.of("include_tasks", "import_tasks", "import_playbook");

    @Override
    public void visitTask(Task task) {
        PathResolver resolver = getContext().getPathResolver();
        if (resolver == null) return;

        for (String pathKey : INCLUDE_KEYS) {
            Object pathObj = task.attributes().get(pathKey);
            if (pathObj == null) continue;
            String path = pathObj.toString().strip();
            if (path.isEmpty()) continue;
            if (!resolver.existsInProject(path)) {
                addLineIssue(task.line(), "Include or import path not found in project: " + path);
            }
            return;
        }
    }
}
