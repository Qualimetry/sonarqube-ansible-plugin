/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.Play;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

/** Block with too many tasks. Tasks are flattened; placeholder. */
@Rule(key = "qa-block-task-limit")
public class BlockMaxTasksCheck extends BaseCheck {

    @Override
    public void visitPlay(Play play) {}
}
