/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

/**
 * Validates playbook structure: root must be a list of plays; each play must be a map
 * with at least one of hosts, tasks, roles, vars.
 */
@Rule(key = "qa-playbook-schema")
public class SchemaCheck extends BaseCheck {

    private static final List<String> PLAY_KEYS = List.of("hosts", "tasks", "roles", "vars");

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
        if (file.parseError() != null) return;
        String content = getContext().getRawContent();
        if (content == null || content.isBlank()) return;
        Object root;
        try {
            root = new Yaml().load(content);
        } catch (Exception e) {
            return;
        }
        if (root == null) return;
        if (!(root instanceof List<?> list)) {
            addFileIssue("Playbook root must be a list of plays.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (!(item instanceof Map<?, ?> map)) {
                addLineIssue(i + 1, "Each play must be a mapping (map) with hosts, tasks, roles, or vars.");
                continue;
            }
            boolean hasPlayKey = false;
            for (Object key : map.keySet()) {
                if (key != null && PLAY_KEYS.contains(key.toString())) {
                    hasPlayKey = true;
                    break;
                }
            }
            if (!hasPlayKey) {
                addLineIssue(i + 1, "Play must contain at least one of: hosts, tasks, roles, vars.");
            }
        }
    }
}
