/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleMeta;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Requires role meta to include galaxy_tags so the role can be filtered with --tags / --skip-tags.
 */
@Rule(key = "qa-role-meta-tags")
public class MetaNoTagsCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
    }

    @Override
    public void visitRoleMeta(RoleMeta meta) {
        if (meta.parseError() != null) return;
        if (meta.galaxyTags().isEmpty()) {
            addFileIssue("Role meta should include galaxy_info.galaxy_tags for --tags / --skip-tags filtering.");
        }
    }
}
