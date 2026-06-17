/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Role directory structure (tasks/, handlers/, etc.). No-op for single-file playbook analysis.
 */
@Rule(key = "qa-role-dir-layout")
public class RoleStructureCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
        // Role layout not in scope
    }
}
