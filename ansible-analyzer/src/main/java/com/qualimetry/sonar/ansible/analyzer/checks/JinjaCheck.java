/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.regex.Pattern;

/**
 * Flags Jinja2 style issues: require spaces inside {{ }} and {% %} (e.g. {{ foo }} not {{foo}}).
 */
@Rule(key = "qa-jinja-format")
public class JinjaCheck extends BaseCheck {

    private static final Pattern NO_SPACE_AFTER_OPEN = Pattern.compile("\\{\\{[^ \\t}]|\\{%[^ %]");
    private static final Pattern NO_SPACE_BEFORE_CLOSE = Pattern.compile("[^ \\t]\\}\\}|[^ %]%\\}");

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
        String content = getContext().getRawContent();
        if (content == null) return;
        String[] lines = content.split("\\r?\\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            boolean bad = NO_SPACE_AFTER_OPEN.matcher(line).find() || NO_SPACE_BEFORE_CLOSE.matcher(line).find();
            if (bad) {
                addLineIssue(i + 1, "Use spaces inside Jinja delimiters (e.g. {{ expr }} not {{expr}}).");
            }
        }
    }
}
