/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Playbook should pass ansible-playbook --syntax-check. Covered by yaml-syntax and parser; this rule is a placeholder.
 */
@Rule(key = "qa-playbook-syntax-run")
public class SyntaxCheckCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
        if (file.parseError() != null) {
            addFileIssue("Playbook has syntax errors; run ansible-playbook --syntax-check.");
        }
    }
}
