/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/** Do not store secrets in plain vars; use vault. Placeholder. */
@Rule(key = "qa-secrets-not-in-vars")
public class NoSecretsInVarsCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {}
}
