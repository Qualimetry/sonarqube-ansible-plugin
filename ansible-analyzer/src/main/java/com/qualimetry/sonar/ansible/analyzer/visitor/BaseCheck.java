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
package com.qualimetry.sonar.ansible.analyzer.visitor;

import com.qualimetry.sonar.ansible.analyzer.parser.model.TextPosition;
import org.sonar.check.Rule;

/**
 * Base class for Ansible analysis checks. Subclasses implement AnsibleVisitor and report via addIssue.
 */
public abstract class BaseCheck implements AnsibleVisitor {

    private AnsibleContext context;

    public void setContext(AnsibleContext context) {
        this.context = context;
    }

    protected AnsibleContext getContext() {
        return context;
    }

    protected String getRuleKey() {
        Rule r = getClass().getAnnotation(Rule.class);
        return r != null ? r.key() : "unknown";
    }

    protected void addLineIssue(int line, String message) {
        if (context != null) {
            context.addIssue(new Issue(getRuleKey(), message, null, line, null, null));
        }
    }

    protected void addFileIssue(String message) {
        if (context != null) {
            context.addIssue(new Issue(getRuleKey(), message, null, null, null, null));
        }
    }

    protected void addIssue(TextPosition position, String message) {
        if (context != null) {
            context.addIssue(new Issue(getRuleKey(), message, position, position.line(), null, null));
        }
    }
}
