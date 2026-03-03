# Qualimetry Ansible Analyzer - SonarQube Plugin

[![CI](https://github.com/Qualimetry/sonarqube-ansible-plugin/actions/workflows/ci.yml/badge.svg)](https://github.com/Qualimetry/sonarqube-ansible-plugin/actions/workflows/ci.yml)

A SonarQube plugin that provides static analysis of Ansible playbooks and task files (`.yml`, `.yaml`) so teams can enforce quality gates in CI/CD pipelines.

Powered by the same analysis engine as the [Qualimetry Ansible Analyzer for VS Code](https://github.com/Qualimetry/vscode-ansible-plugin) and the [Qualimetry Ansible Analyzer for IntelliJ](https://github.com/Qualimetry/intellij-ansible-plugin).

## Features

- **75 rules** covering YAML syntax, naming, safety, and best practices.
- Integrates with SonarQube quality gates and quality profiles.
- Attaches to the existing **ansible** language — coexists with the SonarQube iac plugin.
- Rules use the `qualimetry-ansible` repository key and the `qa-` prefix.
- Licensed under the Apache License 2.0.

## Rule categories

| Category | Examples |
|----------|----------|
| YAML & Syntax | Valid YAML, schema compliance, key ordering, consistent indentation |
| Naming | Task names, variable names, role names, handler names |
| Security | No-log secrets, vault usage, HTTPS required, file permissions |
| Best Practices | Handler usage, tags required, FQCN, deprecated modules |
| Style | Line length, indentation, trailing whitespace, final newline |
| Complexity | Task count limits, play count, block size |
| Galaxy & Roles | Role structure, meta validation, galaxy metadata |

## Compatibility

- **SonarQube 10.x or later** (plugin API version used at build time).
- Also compatible with **SonarCloud** when the Ansible language is available.

## Installation

1. Download the latest `qualimetry-ansible-plugin-<version>.jar` from [Releases](https://github.com/Qualimetry/sonarqube-ansible-plugin/releases).
2. Place the JAR in `SONARQUBE_HOME/extensions/plugins/`.
3. Restart SonarQube.
4. In **Quality Profiles**, select **Qualimetry Ansible** (or **Qualimetry Way**) as the profile for the Ansible language.

## Quality profiles

The plugin provides two built-in profiles:

- **Qualimetry Ansible** - Core rules enabled by default.
- **Qualimetry Way** - Broader set including security and version-pinning rules.

You can customize rules (enable/disable, severity, parameters) in SonarQube under **Quality Profiles**. Rules use the repository key `qualimetry-ansible` and the `qa-` prefix.

## Coexistence with SonarQube iac plugin

This plugin does **not** declare the Ansible language; the SonarQube **iac** (Infrastructure as Code) plugin typically does. This plugin attaches its rule repository and sensor to the existing **ansible** language. Both plugins can be installed: the iac plugin owns the language key, and this plugin adds the Qualimetry rule set.

## Avoiding plain YAML

Only files SonarQube has assigned to the **ansible** language are analyzed. If a file is marked as ansible but does not look like a playbook (no plays) and is not a role meta file (`meta/main.yml`), the sensor skips it to avoid noise on plain config YAML.

## Also available

The same analysis engine powers editor extensions for real-time feedback:

- **[VS Code extension](https://github.com/Qualimetry/vscode-ansible-plugin)** — catch issues as you type, before you commit.
- **[IntelliJ plugin](https://github.com/Qualimetry/intellij-ansible-plugin)** — real-time analysis in JetBrains IDEs and Qodana CI/CD.

Rule keys and severities align across all three tools so findings are directly comparable.

## Building from source

Requires JDK 17+ and Maven 3.6+.

```bash
mvn clean package -DskipTests
```

The packaged plugin JAR is at `ansible-plugin/target/qualimetry-ansible-plugin-<version>.jar`.

To run the full test suite:

```bash
mvn clean verify
```

## CI and releases

The [CI](https://github.com/Qualimetry/sonarqube-ansible-plugin/actions/workflows/ci.yml) workflow runs on every push and pull request to `main`: it builds and runs tests on Java 17, and uploads the plugin JAR as an artifact. A **GitHub Release** (tag + release notes + JAR) is created **only when a commit message starts with `release:`** (e.g. `release: 1.0.0`).

## Contributing

Issues and feature requests are welcome. This project does not accept pull requests, commits, or other code contributions from third parties; the repository is maintained by the Qualimetry team only.

## License

This plugin is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

Copyright 2026 SHAZAM Analytics Ltd
