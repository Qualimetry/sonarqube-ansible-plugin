/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.Task;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.Map;
import java.util.regex.Pattern;

/** set_fact names should follow convention. */
@Rule(key = "qa-fact-name-format")
public class FactNamingCheck extends BaseCheck {

    private static final Pattern VALID = Pattern.compile("^[a-z][a-z0-9_]*$");

    @Override
    public void visitTask(Task task) {
        String moduleKey = task.moduleKey();
        if (moduleKey == null) return;
        String base = moduleKey.contains(".") ? moduleKey.substring(moduleKey.lastIndexOf('.') + 1) : moduleKey;
        if (!"set_fact".equals(base)) return;
        Map<String, Object> attrs = task.attributes();
        if (attrs == null) return;
        Object facts = attrs.get(moduleKey);
        if (facts == null) facts = attrs.get("set_fact");
        if (!(facts instanceof Map<?, ?> m)) return;
        for (Object k : m.keySet()) {
            if (k instanceof String name && !VALID.matcher(name).matches()) {
                addLineIssue(task.line(), "Fact name should be lowercase with letters, numbers, underscores: " + name);
                return;
            }
        }
    }
}
