/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/** Use role defaults not vars. Placeholder. */
@Rule(key = "qa-role-defaults-dir")
public class DefaultsNotVarsCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {}
}
