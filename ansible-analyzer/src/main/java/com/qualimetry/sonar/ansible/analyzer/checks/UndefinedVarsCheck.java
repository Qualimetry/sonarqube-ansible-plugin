/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/** Use of undefined variables. Placeholder. */
@Rule(key = "qa-define-referenced-vars")
public class UndefinedVarsCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {}
}
