/*
 * Copyright 2026 SHAZAM Analytics Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qualimetry.sonar.ansible.plugin;

import com.qualimetry.sonar.ansible.analyzer.checks.CheckList;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Defines the Ansible rules repository for SonarQube.
 */
public class AnsibleRulesDefinition implements RulesDefinition {

    private static final String RESOURCE_BASE =
            "/com/qualimetry/sonar/ansible/analyzer/checks/";

    /** Display names for rules. */
    private static final Map<String, String> RULE_DISPLAY_NAMES = Map.ofEntries(
            Map.entry("qa-explicit-mode-owner", "Avoid implicit file mode or ownership"),
            Map.entry("qa-become-non-root-user", "Become user must not be root"),
            Map.entry("qa-block-task-limit", "Limit number of tasks in block"),
            Map.entry("qa-use-module-not-command", "Use Ansible module instead of command"),
            Map.entry("qa-command-not-shell-when-possible", "Prefer shell module over command for shell features"),
            Map.entry("qa-limit-task-attributes", "Reduce task or playbook complexity"),
            Map.entry("qa-even-spaces-indent", "Use consistent indentation"),
            Map.entry("qa-role-defaults-dir", "Use defaults/ not vars/ for role defaults"),
            Map.entry("qa-bare-var-in-condition", "Use bare variable in when/loop not {{ var }}"),
            Map.entry("qa-delegate-to-localhost", "Use delegate_to localhost instead of local_action"),
            Map.entry("qa-replace-deprecated-module", "Avoid deprecated module usage"),
            Map.entry("qa-replace-deprecated-param", "Avoid deprecated module parameters"),
            Map.entry("qa-check-length-not-empty", "Prefer length or presence over empty string compare"),
            Map.entry("qa-fact-name-format", "Follow fact naming conventions"),
            Map.entry("qa-yml-extension", "Follow file naming conventions"),
            Map.entry("qa-full-module-name", "Use fully qualified collection names"),
            Map.entry("qa-role-galaxy-deps", "Fix Galaxy metadata or dependencies"),
            Map.entry("qa-handler-has-name", "Follow handler naming conventions"),
            Map.entry("qa-env-block-not-inline", "Avoid inline environment variables"),
            Map.entry("qa-import-versus-include", "Prefer import_tasks over include_tasks when appropriate"),
            Map.entry("qa-explicit-error-handling", "Avoid ignore_errors for control flow"),
            Map.entry("qa-jinja-format", "Fix Jinja2 template usage"),
            Map.entry("qa-task-name-first", "Task name before module key"),
            Map.entry("qa-pin-version-not-latest", "Avoid unversioned latest in package installs"),
            Map.entry("qa-max-line-length", "Limit line length"),
            Map.entry("qa-avoid-literal-bool-compare", "Prefer when: var over when: var == \"yes\""),
            Map.entry("qa-includes-resolve", "Fix role or playbook load failure"),
            Map.entry("qa-prefix-loop-var", "Prefix loop variable names"),
            Map.entry("qa-role-meta-format", "Fix meta/main.yml content"),
            Map.entry("qa-role-meta-tags", "Add tags to meta/main.yml"),
            Map.entry("qa-role-meta-runtime", "Fix meta runtime configuration"),
            Map.entry("qa-role-meta-video-links", "Remove or fix meta video links"),
            Map.entry("qa-limit-plays", "Limit number of plays per playbook"),
            Map.entry("qa-limit-tasks-per-play", "Limit number of tasks per play"),
            Map.entry("qa-task-has-name", "Task must have a name"),
            Map.entry("qa-file-ends-newline", "End file with newline"),
            Map.entry("qa-command-changed-when", "Avoid changed_when with only static values"),
            Map.entry("qa-unique-tasks", "Remove duplicate task definitions"),
            Map.entry("qa-command-args-form", "Avoid free-form command or shell"),
            Map.entry("qa-handler-for-notify", "Define handler when notified"),
            Map.entry("qa-require-https", "Use HTTPS instead of HTTP"),
            Map.entry("qa-when-bare-variable", "Use bare variable in when not {{ var }}"),
            Map.entry("qa-no-log-secrets", "Do not log sensitive data"),
            Map.entry("qa-absolute-or-role-paths", "Avoid relative paths in critical arguments"),
            Map.entry("qa-explicit-owner-group", "Set owner and group explicitly"),
            Map.entry("qa-secrets-not-in-vars", "Do not store secrets in plain vars"),
            Map.entry("qa-spaces-not-tabs", "Disallow tab characters"),
            Map.entry("qa-strip-trailing-whitespace", "Remove trailing whitespace"),
            Map.entry("qa-safe-file-read", "Avoid unsafe read of file contents"),
            Map.entry("qa-restrict-world-write", "Avoid world-writable permissions"),
            Map.entry("qa-no-vars-prompt", "Avoid prompting for input"),
            Map.entry("qa-builtin-modules-only", "Restrict to ansible.builtin modules"),
            Map.entry("qa-pin-package-version", "Pin package versions instead of latest"),
            Map.entry("qa-yaml-parse-error", "Fix parser error"),
            Map.entry("qa-become-with-user", "Apply become consistently"),
            Map.entry("qa-playbook-yml-extension", "Use .yml or .yaml for playbooks"),
            Map.entry("qa-group-tasks-in-block", "Prefer block for grouping tasks"),
            Map.entry("qa-play-has-tags", "Include required tags"),
            Map.entry("qa-sudo-nopasswd-limit", "Restrict sudo NOPASSWD usage"),
            Map.entry("qa-restrict-file-mode", "Avoid risky file permissions"),
            Map.entry("qa-numeric-file-mode", "Avoid risky octal modes"),
            Map.entry("qa-shell-pipe-safe", "Avoid risky shell piping"),
            Map.entry("qa-role-name-format", "Follow role naming conventions"),
            Map.entry("qa-role-dir-layout", "Follow role directory structure"),
            Map.entry("qa-run-once-documented", "Use run_once with care"),
            Map.entry("qa-runtime-sanity", "Fix sanity check failure"),
            Map.entry("qa-playbook-schema", "Fix schema validation"),
            Map.entry("qa-playbook-syntax-run", "Fix syntax check failure"),
            Map.entry("qa-task-name-min-chars", "Use sufficiently long task names"),
            Map.entry("qa-define-referenced-vars", "Define or pass undefined variables"),
            Map.entry("qa-remove-unused-vars", "Remove or use unused variables"),
            Map.entry("qa-variable-name-format", "Follow variable naming conventions"),
            Map.entry("qa-secrets-in-vault", "Use vault for secrets"),
            Map.entry("qa-diagnostic-warning", "Address analyzer warning"),
            Map.entry("qa-valid-yaml", "Valid YAML structure required")
    );

    /** Severity by rule key (Sonar uses String constants). Rules not listed default to MINOR. */
    private static final Map<String, String> RULE_SEVERITIES = buildSeverityMap();

    /** Tags by rule key. All rules get "ansible"; security rules also get "security" and "cwe"; style/naming get "convention". */
    private static final Map<String, String[]> RULE_TAGS = buildRuleTagsMap();

    /** Rule type by rule key (VULNERABILITY, BUG, or CODE_SMELL). Unlisted rules default to CODE_SMELL. */
    private static final Map<String, RuleType> RULE_TYPES = buildRuleTypesMap();

    private static Map<String, String> buildSeverityMap() {
        Map<String, String> m = new HashMap<>();
        // BLOCKER: secrets / sensitive data exposure
        m.put("qa-no-log-secrets", Severity.BLOCKER);
        m.put("qa-secrets-not-in-vars", Severity.BLOCKER);
        m.put("qa-require-https", Severity.CRITICAL);
        m.put("qa-safe-file-read", Severity.CRITICAL);
        m.put("qa-restrict-world-write", Severity.CRITICAL);
        m.put("qa-become-non-root-user", Severity.CRITICAL);
        m.put("qa-restrict-file-mode", Severity.CRITICAL);
        m.put("qa-numeric-file-mode", Severity.CRITICAL);
        m.put("qa-shell-pipe-safe", Severity.CRITICAL);
        m.put("qa-sudo-nopasswd-limit", Severity.CRITICAL);
        m.put("qa-secrets-in-vault", Severity.CRITICAL);
        m.put("qa-no-vars-prompt", Severity.CRITICAL);
        m.put("qa-valid-yaml", Severity.MAJOR);
        m.put("qa-yaml-parse-error", Severity.MAJOR);
        m.put("qa-explicit-error-handling", Severity.MAJOR);
        m.put("qa-command-changed-when", Severity.MAJOR);
        m.put("qa-includes-resolve", Severity.MAJOR);
        m.put("qa-define-referenced-vars", Severity.MAJOR);
        m.put("qa-replace-deprecated-module", Severity.MAJOR);
        m.put("qa-handler-for-notify", Severity.MAJOR);
        m.put("qa-absolute-or-role-paths", Severity.MAJOR);
        m.put("qa-playbook-schema", Severity.MAJOR);
        m.put("qa-playbook-syntax-run", Severity.MAJOR);
        m.put("qa-command-args-form", Severity.MAJOR);
        m.put("qa-explicit-owner-group", Severity.MAJOR);
        m.put("qa-use-module-not-command", Severity.MAJOR);
        m.put("qa-become-with-user", Severity.MAJOR);
        m.put("qa-replace-deprecated-param", Severity.MAJOR);
        m.put("qa-delegate-to-localhost", Severity.MAJOR);
        m.put("qa-task-name-first", Severity.MINOR);
        m.put("qa-task-has-name", Severity.MINOR);
        m.put("qa-even-spaces-indent", Severity.MINOR);
        m.put("qa-playbook-yml-extension", Severity.MINOR);
        m.put("qa-full-module-name", Severity.MINOR);
        m.put("qa-command-not-shell-when-possible", Severity.MINOR);
        m.put("qa-bare-var-in-condition", Severity.MINOR);
        m.put("qa-when-bare-variable", Severity.MINOR);
        m.put("qa-prefix-loop-var", Severity.MINOR);
        m.put("qa-avoid-literal-bool-compare", Severity.MINOR);
        m.put("qa-limit-tasks-per-play", Severity.MINOR);
        m.put("qa-limit-plays", Severity.MINOR);
        m.put("qa-unique-tasks", Severity.MINOR);
        m.put("qa-play-has-tags", Severity.MINOR);
        m.put("qa-variable-name-format", Severity.MINOR);
        m.put("qa-handler-has-name", Severity.MINOR);
        m.put("qa-role-name-format", Severity.MINOR);
        m.put("qa-import-versus-include", Severity.MINOR);
        m.put("qa-limit-task-attributes", Severity.MINOR);
        m.put("qa-check-length-not-empty", Severity.MINOR);
        m.put("qa-group-tasks-in-block", Severity.MINOR);
        m.put("qa-jinja-format", Severity.MINOR);
        m.put("qa-explicit-mode-owner", Severity.MINOR);
        m.put("qa-block-task-limit", Severity.MINOR);
        m.put("qa-pin-version-not-latest", Severity.MINOR);
        m.put("qa-pin-package-version", Severity.MINOR);
        m.put("qa-role-dir-layout", Severity.MINOR);
        m.put("qa-yml-extension", Severity.MINOR);
        m.put("qa-fact-name-format", Severity.MINOR);
        m.put("qa-remove-unused-vars", Severity.MINOR);
        m.put("qa-role-defaults-dir", Severity.MINOR);
        m.put("qa-env-block-not-inline", Severity.MINOR);
        m.put("qa-builtin-modules-only", Severity.MINOR);
        m.put("qa-run-once-documented", Severity.MINOR);
        m.put("qa-spaces-not-tabs", Severity.INFO);
        m.put("qa-file-ends-newline", Severity.INFO);
        m.put("qa-strip-trailing-whitespace", Severity.INFO);
        m.put("qa-max-line-length", Severity.INFO);
        m.put("qa-task-name-min-chars", Severity.INFO);
        m.put("qa-role-meta-format", Severity.INFO);
        m.put("qa-role-meta-tags", Severity.INFO);
        m.put("qa-role-meta-runtime", Severity.INFO);
        m.put("qa-role-meta-video-links", Severity.INFO);
        m.put("qa-role-galaxy-deps", Severity.INFO);
        m.put("qa-runtime-sanity", Severity.INFO);
        m.put("qa-diagnostic-warning", Severity.INFO);
        return Map.copyOf(m);
    }

    private static Map<String, String[]> buildRuleTagsMap() {
        Map<String, Set<String>> map = new HashMap<>();
        String[] securityRules = {
                "qa-no-log-secrets", "qa-secrets-not-in-vars", "qa-secrets-in-vault", "qa-require-https",
                "qa-safe-file-read", "qa-restrict-world-write", "qa-become-non-root-user", "qa-restrict-file-mode",
                "qa-numeric-file-mode", "qa-shell-pipe-safe", "qa-sudo-nopasswd-limit", "qa-no-vars-prompt",
                "qa-command-args-form", "qa-use-module-not-command", "qa-become-with-user", "qa-explicit-owner-group",
                "qa-absolute-or-role-paths"
        };
        String[] conventionRules = {
                "qa-spaces-not-tabs", "qa-strip-trailing-whitespace", "qa-even-spaces-indent", "qa-file-ends-newline",
                "qa-max-line-length", "qa-task-name-first", "qa-task-has-name", "qa-task-name-min-chars",
                "qa-variable-name-format", "qa-handler-has-name", "qa-role-name-format", "qa-fact-name-format",
                "qa-yml-extension", "qa-playbook-yml-extension", "qa-valid-yaml"
        };
        for (String key : securityRules) {
            map.put(key, Set.of("ansible", "security", "cwe"));
        }
        for (String key : conventionRules) {
            map.put(key, Set.of("ansible", "convention"));
        }
        Map<String, String[]> result = new HashMap<>();
        for (Map.Entry<String, Set<String>> e : map.entrySet()) {
            result.put(e.getKey(), e.getValue().toArray(new String[0]));
        }
        return Map.copyOf(result);
    }

    private static Map<String, RuleType> buildRuleTypesMap() {
        Map<String, RuleType> m = new HashMap<>();
        // Security-sensitive: VULNERABILITY
        String[] vulnerabilityRules = {
                "qa-no-log-secrets", "qa-secrets-not-in-vars", "qa-secrets-in-vault", "qa-require-https",
                "qa-safe-file-read", "qa-restrict-world-write", "qa-become-non-root-user", "qa-restrict-file-mode",
                "qa-numeric-file-mode", "qa-shell-pipe-safe", "qa-sudo-nopasswd-limit", "qa-no-vars-prompt",
                "qa-command-args-form", "qa-use-module-not-command", "qa-become-with-user", "qa-explicit-owner-group",
                "qa-absolute-or-role-paths"
        };
        for (String key : vulnerabilityRules) {
            m.put(key, RuleType.VULNERABILITY);
        }
        // Reliability / incorrect behavior: BUG
        m.put("qa-valid-yaml", RuleType.BUG);
        m.put("qa-yaml-parse-error", RuleType.BUG);
        m.put("qa-define-referenced-vars", RuleType.BUG);
        m.put("qa-includes-resolve", RuleType.BUG);
        m.put("qa-handler-for-notify", RuleType.BUG);
        m.put("qa-explicit-error-handling", RuleType.BUG);
        m.put("qa-command-changed-when", RuleType.BUG);
        m.put("qa-playbook-schema", RuleType.BUG);
        m.put("qa-playbook-syntax-run", RuleType.BUG);
        return Map.copyOf(m);
    }

    private static RuleType getTypeForRule(String ruleKey) {
        return RULE_TYPES.getOrDefault(ruleKey, RuleType.CODE_SMELL);
    }

    private static String[] getTagsForRule(String ruleKey) {
        String[] tags = RULE_TAGS.get(ruleKey);
        return tags != null ? tags : new String[]{"ansible"};
    }

    @Override
    public void define(Context context) {
        var repo = context.createRepository(CheckList.REPOSITORY_KEY, AnsiblePluginConstants.ANSIBLE_LANGUAGE_KEY)
                .setName(CheckList.REPOSITORY_NAME);

        new RulesDefinitionAnnotationLoader()
                .load(repo, CheckList.getAllChecks().toArray(new Class<?>[0]));

        for (var rule : repo.rules()) {
            String htmlPath = RESOURCE_BASE + rule.key() + ".html";
            try (InputStream is = getClass().getResourceAsStream(htmlPath)) {
                if (is != null) {
                    rule.setHtmlDescription(readStream(is));
                }
            } catch (java.io.IOException e) {
                throw new IllegalStateException("Failed to read rule description: " + htmlPath, e);
            }
            rule.setSeverity(RULE_SEVERITIES.getOrDefault(rule.key(), Severity.MINOR));
            rule.setName(getDisplayName(rule.key()));
            rule.addTags(getTagsForRule(rule.key()));
            rule.setType(getTypeForRule(rule.key()));
        }

        repo.done();
    }

    private static String getDisplayName(String key) {
        String name = RULE_DISPLAY_NAMES.get(key);
        return name != null ? name : ruleKeyToName(key);
    }

    private static String ruleKeyToName(String key) {
        if (key == null || key.isEmpty()) return key;
        StringBuilder sb = new StringBuilder();
        boolean cap = true;
        for (char c : key.toCharArray()) {
            if (c == '-') {
                sb.append(' ');
                cap = true;
            } else {
                sb.append(cap ? Character.toUpperCase(c) : c);
                cap = false;
            }
        }
        return sb.toString();
    }

    private static String readStream(InputStream is) {
        try (var reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read rule description", e);
        }
    }
}
