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
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry of all Ansible analysis checks. Default profile uses getDefaultRuleKeys().
 */
public final class CheckList {

    public static final String REPOSITORY_KEY = "qualimetry-ansible";
    public static final String REPOSITORY_NAME = "Qualimetry Ansible";

    private CheckList() {
    }

    public static List<Class<? extends BaseCheck>> getAllChecks() {
        return List.of(
                YamlSyntaxCheck.class,
                NoTabsCheck.class,
                NewlineAtEndOfFileCheck.class,
                NoTrailingWhitespaceCheck.class,
                KeyOrderCheck.class,
                NameCheck.class,
                TaskNameMinLengthCheck.class,
                NoFreeFormCheck.class,
                DeprecatedLocalActionCheck.class,
                PartialBecomeCheck.class,
                NoLogPasswordCheck.class,
                IgnoreErrorsCheck.class,
                NoChangedWhenCheck.class,
                ConsistentIndentationCheck.class,
                RiskyFilePermissionsCheck.class,
                RiskyOctalCheck.class,
                PlaybookExtensionCheck.class,
                // Phase 4 opt-in rules
                FqcnCheck.class,
                CommandInsteadOfModuleCheck.class,
                CommandInsteadOfShellCheck.class,
                RoleNameCheck.class,
                DeprecatedModuleCheck.class,
                DeprecatedBareVarsCheck.class,
                RiskyShellPipeCheck.class,
                NoJinjaWhenCheck.class,
                LoopVarPrefixCheck.class,
                DeprecatedParameterCheck.class,
                // Phase 5 opt-in rules
                LineLengthCheck.class,
                MaxTasksPerPlayCheck.class,
                MaxPlaysPerPlaybookCheck.class,
                BecomeUserNotRootCheck.class,
                NoDuplicateTasksCheck.class,
                RequiredTagsCheck.class,
                NoWorldWritableCheck.class,
                NoHttpWithoutTlsCheck.class,
                LiteralCompareCheck.class,
                // Phase 8: extended rule set
                VarNamingCheck.class,
                HandlerNamingCheck.class,
                FileNamingCheck.class,
                NoSameOwnerCheck.class,
                OnlyBuiltinsCheck.class,
                AvoidImplicitCheck.class,
                NoRelativePathsCheck.class,
                RunOnceCheck.class,
                EmptyStringCompareCheck.class,
                InlineEnvVarCheck.class,
                NoPromptingCheck.class,
                ComplexityCheck.class,
                PackageLatestCheck.class,
                LatestCheck.class,
                IncludeVsImportCheck.class,
                NoUnsafeReadFileCheck.class,
                SyntaxCheckCheck.class,
                ParserErrorCheck.class,
                WarningCheck.class,
                MetaRuntimeCheck.class,
                GalaxyCheck.class,
                MetaIncorrectCheck.class,
                MetaNoTagsCheck.class,
                MetaVideoLinksCheck.class,
                RoleStructureCheck.class,
                SchemaCheck.class,
                SanityCheck.class,
                LoadFailureCheck.class,
                NoSecretsInVarsCheck.class,
                VaultForSecretsCheck.class,
                RestrictSudoNopasswdCheck.class,
                BlockMaxTasksCheck.class,
                DefaultsNotVarsCheck.class,
                JinjaCheck.class,
                NoHandlerCheck.class,
                UnusedVarsCheck.class,
                UndefinedVarsCheck.class,
                PreferBlockCheck.class,
                FactNamingCheck.class
        );
    }

    /** Rule keys enabled in the default Qualimetry Ansible profile. */
    public static List<String> getDefaultRuleKeys() {
        return List.of(
                "qa-valid-yaml",
                "qa-spaces-not-tabs",
                "qa-file-ends-newline",
                "qa-strip-trailing-whitespace",
                "qa-task-name-first",
                "qa-task-has-name",
                "qa-task-name-min-chars",
                // qa-command-args-form, qa-use-module-not-command, qa-command-not-shell-when-possible: duplicate Sonar "Allowing command execution"
                "qa-delegate-to-localhost",
                // qa-become-with-user, qa-become-non-root-user: duplicate Sonar "Process privilege escalations"
                // qa-no-log-secrets, qa-secrets-not-in-vars, qa-secrets-in-vault: duplicate Sonar "Credentials hard-coded"
                "qa-explicit-error-handling",
                "qa-command-changed-when",
                "qa-even-spaces-indent",
                // qa-restrict-file-mode, qa-numeric-file-mode, qa-restrict-world-write, qa-explicit-mode-owner: duplicate Sonar "Loose POSIX file permissions"
                "qa-playbook-yml-extension",
                "qa-full-module-name",
                "qa-max-line-length",
                "qa-limit-tasks-per-play",
                "qa-limit-plays",
                "qa-unique-tasks",
                // qa-require-https: duplicate Sonar "Clear-text protocols" / "Server certificates verified"
                "qa-avoid-literal-bool-compare",
                "qa-replace-deprecated-module",
                "qa-bare-var-in-condition",
                "qa-when-bare-variable",
                "qa-prefix-loop-var",
                "qa-play-has-tags",
                "qa-variable-name-format",
                "qa-handler-has-name",
                "qa-role-name-format",
                "qa-safe-file-read",
                "qa-explicit-owner-group",
                "qa-no-vars-prompt",
                "qa-absolute-or-role-paths",
                "qa-import-versus-include",
                "qa-limit-task-attributes",
                "qa-check-length-not-empty",
                "qa-group-tasks-in-block",
                "qa-jinja-format",
                "qa-playbook-schema",
                "qa-shell-pipe-safe",
                "qa-replace-deprecated-param",
                "qa-handler-for-notify",
                "qa-block-task-limit",
                // qa-pin-version-not-latest, qa-pin-package-version: duplicate Sonar "Specific version tag for image"
                "qa-yaml-parse-error",
                "qa-includes-resolve",
                "qa-define-referenced-vars",
                "qa-remove-unused-vars",
                "qa-role-dir-layout",
                "qa-yml-extension",
                "qa-fact-name-format"
        );
    }

    /** Rule keys for the Qualimetry Way profile (recommended full set including security and version-pinning rules). */
    public static List<String> getQualimetryWayRuleKeys() {
        return List.of(
                "qa-valid-yaml",
                "qa-spaces-not-tabs",
                "qa-file-ends-newline",
                "qa-strip-trailing-whitespace",
                "qa-task-name-first",
                "qa-task-has-name",
                "qa-task-name-min-chars",
                "qa-command-args-form",
                "qa-use-module-not-command",
                "qa-command-not-shell-when-possible",
                "qa-delegate-to-localhost",
                "qa-become-with-user",
                "qa-become-non-root-user",
                "qa-explicit-error-handling",
                "qa-command-changed-when",
                "qa-even-spaces-indent",
                "qa-restrict-file-mode",
                "qa-numeric-file-mode",
                "qa-restrict-world-write",
                "qa-explicit-mode-owner",
                "qa-playbook-yml-extension",
                "qa-full-module-name",
                "qa-max-line-length",
                "qa-limit-tasks-per-play",
                "qa-limit-plays",
                "qa-unique-tasks",
                "qa-require-https",
                "qa-no-log-secrets",
                "qa-secrets-not-in-vars",
                "qa-secrets-in-vault",
                "qa-avoid-literal-bool-compare",
                "qa-replace-deprecated-module",
                "qa-bare-var-in-condition",
                "qa-when-bare-variable",
                "qa-prefix-loop-var",
                "qa-play-has-tags",
                "qa-variable-name-format",
                "qa-handler-has-name",
                "qa-role-name-format",
                "qa-safe-file-read",
                "qa-explicit-owner-group",
                "qa-no-vars-prompt",
                "qa-absolute-or-role-paths",
                "qa-import-versus-include",
                "qa-limit-task-attributes",
                "qa-check-length-not-empty",
                "qa-group-tasks-in-block",
                "qa-jinja-format",
                "qa-playbook-schema",
                "qa-shell-pipe-safe",
                "qa-replace-deprecated-param",
                "qa-handler-for-notify",
                "qa-block-task-limit",
                "qa-pin-version-not-latest",
                "qa-pin-package-version",
                "qa-yaml-parse-error",
                "qa-includes-resolve",
                "qa-define-referenced-vars",
                "qa-remove-unused-vars",
                "qa-role-dir-layout",
                "qa-yml-extension",
                "qa-fact-name-format",
                "qa-sudo-nopasswd-limit",
                "qa-diagnostic-warning"
        );
    }

    /** All rule keys from every check (for Qualimetry All profile and ITs). */
    public static List<String> getAllRuleKeys() {
        List<String> keys = new ArrayList<>();
        for (Class<? extends BaseCheck> clazz : getAllChecks()) {
            Rule r = clazz.getAnnotation(Rule.class);
            if (r != null && r.key() != null && !r.key().isEmpty()) {
                keys.add(r.key());
            }
        }
        return List.copyOf(keys);
    }
}
