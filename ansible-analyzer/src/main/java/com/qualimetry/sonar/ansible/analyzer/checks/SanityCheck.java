/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Placeholder for ansible-test sanity results. This analyzer does not run ansible-test sanity;
 * run it locally and fix reported issues. Optionally reports one informational message per file.
 */
@Rule(key = "qa-runtime-sanity")
public class SanityCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
        addFileIssue("Runtime sanity is not run by this analyzer; use ansible-test sanity locally.");
    }
}
