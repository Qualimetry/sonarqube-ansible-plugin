/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleMeta;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Validates runtime-related options in role meta (e.g. allow_duplicates, dependency options).
 */
@Rule(key = "qa-role-meta-runtime")
public class MetaRuntimeCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
    }

    @Override
    public void visitRoleMeta(RoleMeta meta) {
        if (meta.parseError() != null) return;
        if (Boolean.TRUE.equals(meta.allowDuplicates())) {
            addFileIssue("allow_duplicates: true may cause unexpected behavior; document if intentional.");
        }
    }
}
