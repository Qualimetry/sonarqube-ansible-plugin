/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleMeta;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Validates role meta/main.yml structure: require galaxy_info, valid YAML.
 */
@Rule(key = "qa-role-meta-format")
public class MetaIncorrectCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
    }

    @Override
    public void visitRoleMeta(RoleMeta meta) {
        if (meta.parseError() != null) {
            int line = meta.parseError().line();
            if (line > 0) {
                addLineIssue(line, meta.parseError().message());
            } else {
                addFileIssue(meta.parseError().message());
            }
            return;
        }
        if (meta.galaxyInfo().isEmpty()) {
            addFileIssue("Role meta should contain galaxy_info.");
        }
    }
}
