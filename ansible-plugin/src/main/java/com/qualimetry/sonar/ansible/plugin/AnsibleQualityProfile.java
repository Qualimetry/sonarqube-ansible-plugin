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
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import java.util.LinkedHashSet;
import java.util.Set;

/** Built-in quality profiles for Ansible: Qualimetry Ansible and Qualimetry Way. */
public class AnsibleQualityProfile implements BuiltInQualityProfilesDefinition {

    static final String PROFILE_NAME = "Qualimetry Ansible";
    static final String QUALIMETRY_WAY_NAME = "Qualimetry Way";

    @Override
    public void define(Context context) {
        defineProfile(context, PROFILE_NAME, new LinkedHashSet<>(CheckList.getDefaultRuleKeys()));
        defineProfile(context, QUALIMETRY_WAY_NAME, new LinkedHashSet<>(CheckList.getQualimetryWayRuleKeys()));
    }

    private static void defineProfile(Context context, String name, Set<String> ruleKeys) {
        var profile = context.createBuiltInQualityProfile(name, AnsiblePluginConstants.ANSIBLE_LANGUAGE_KEY);
        profile.setDefault(false);
        for (String ruleKey : ruleKeys) {
            profile.activateRule(CheckList.REPOSITORY_KEY, ruleKey);
        }
        profile.done();
    }
}
