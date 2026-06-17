/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/** Sensitive data should use vault. Placeholder. */
@Rule(key = "qa-secrets-in-vault")
public class VaultForSecretsCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {}
}
