/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.Play;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/** Use block for grouping. Placeholder. */
@Rule(key = "qa-group-tasks-in-block")
public class PreferBlockCheck extends BaseCheck {

    @Override
    public void visitPlay(Play play) {}
}
