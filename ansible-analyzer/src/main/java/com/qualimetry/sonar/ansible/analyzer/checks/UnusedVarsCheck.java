/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/** Variables defined but not used. Placeholder. */
@Rule(key = "qa-remove-unused-vars")
public class UnusedVarsCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {}
}
