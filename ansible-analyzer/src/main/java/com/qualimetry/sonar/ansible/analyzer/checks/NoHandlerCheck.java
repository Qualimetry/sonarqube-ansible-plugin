/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.Task;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/** Tasks that notify should have matching handler. Placeholder. */
@Rule(key = "qa-handler-for-notify")
public class NoHandlerCheck extends BaseCheck {

    @Override
    public void visitTask(Task task) {}
}
