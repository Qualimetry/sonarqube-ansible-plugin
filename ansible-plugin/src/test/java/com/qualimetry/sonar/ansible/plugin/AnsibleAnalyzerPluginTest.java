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

import org.junit.jupiter.api.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarRuntime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AnsibleAnalyzerPluginTest {

    private Plugin.Context createContext() {
        return new Plugin.Context(mock(SonarRuntime.class));
    }

    @Test
    void shouldRegisterThreeExtensions() {
        Plugin.Context context = createContext();
        new AnsibleAnalyzerPlugin().define(context);
        assertThat(context.getExtensions()).hasSize(3);
    }

    @Test
    void shouldRegisterRulesAndProfile() {
        Plugin.Context context = createContext();
        new AnsibleAnalyzerPlugin().define(context);
        assertThat(context.getExtensions()).contains(AnsibleRulesDefinition.class, AnsibleQualityProfile.class);
    }

    @Test
    void shouldRegisterSensor() {
        Plugin.Context context = createContext();
        new AnsibleAnalyzerPlugin().define(context);
        assertThat(context.getExtensions()).contains(AnsibleSensor.class);
    }
}
