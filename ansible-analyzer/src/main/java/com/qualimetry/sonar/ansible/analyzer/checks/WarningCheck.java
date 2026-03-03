/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

/**
 * Configurable diagnostic warning. When the rule parameter "message" is set, reports that message once per file.
 */
@Rule(key = "qa-diagnostic-warning")
public class WarningCheck extends BaseCheck {

    @RuleProperty(
            key = "message",
            description = "Custom message to report when this rule is evaluated (leave empty for no-op).",
            defaultValue = "")
    protected String message = "";

    void setMessageForTesting(String message) {
        this.message = message;
    }

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
        if (message != null && !message.isBlank()) {
            addFileIssue(message);
        }
    }
}
