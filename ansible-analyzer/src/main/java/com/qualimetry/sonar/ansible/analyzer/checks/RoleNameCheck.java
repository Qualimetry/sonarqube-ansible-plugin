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

import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleRef;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.regex.Pattern;

/**
 * Role names should follow convention: lowercase, alphanumeric and underscores (or FQCN with dots).
 */
@Rule(key = "qa-role-name-format")
public class RoleNameCheck extends BaseCheck {

    /** Simple role name: lowercase, digits, underscores only. FQCN (contains dot) is allowed. */
    private static final Pattern SIMPLE_ROLE_NAME = Pattern.compile("^[a-z0-9_]+$");
    private static final Pattern FQCN_ROLE = Pattern.compile("^[a-z0-9_]+\\.[a-z0-9_]+(\\.[a-z0-9_]+)*$");

    @Override
    public void visitRoleRef(RoleRef roleRef) {
        String name = roleRef.roleName();
        if (name == null || name.isBlank()) return;
        if (FQCN_ROLE.matcher(name).matches()) return; // collection.role format
        if (SIMPLE_ROLE_NAME.matcher(name).matches()) return;
        addLineIssue(roleRef.line(), "Role name should be lowercase with only letters, numbers, and underscores (e.g. my_role).");
    }
}
