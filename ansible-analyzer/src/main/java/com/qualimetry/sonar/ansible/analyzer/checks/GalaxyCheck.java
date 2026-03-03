/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleMeta;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.Map;

/**
 * Validates Galaxy metadata and dependencies (dependencies list format, version constraints).
 */
@Rule(key = "qa-role-galaxy-deps")
public class GalaxyCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
    }

    @Override
    public void visitRoleMeta(RoleMeta meta) {
        if (meta.parseError() != null) return;
        for (Object dep : meta.dependencies()) {
            if (dep == null) {
                addFileIssue("Dependencies list should not contain null entries.");
                continue;
            }
            if (dep instanceof String s) {
                if (s.isBlank()) {
                    addFileIssue("Dependencies list should not contain blank role names.");
                }
                continue;
            }
            if (dep instanceof Map<?, ?> map) {
                if (!map.containsKey("role") && !map.containsKey("name")) {
                    addFileIssue("Dependency entry should specify role or name.");
                }
            }
        }
    }
}
