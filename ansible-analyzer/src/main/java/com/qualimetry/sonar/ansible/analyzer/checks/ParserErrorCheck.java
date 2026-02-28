/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/**
 * Report YAML/parser errors. Overlaps with yaml-syntax.
 */
@Rule(key = "qa-yaml-parse-error")
public class ParserErrorCheck extends BaseCheck {

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
        if (file.parseError() != null) {
            addLineIssue(file.parseError().line(), "Parser error: " + file.parseError().message());
        }
    }
}
