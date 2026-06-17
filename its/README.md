# Integration test projects (ITS)

- **compliant**: Playbook YAML files that satisfy the plugin rules; analysis should pass or have few issues.
- **noncompliant**: Playbook YAML files that violate rules (e.g. tabs, invalid YAML, trailing whitespace); analysis should report issues.

The plugin adds a rule repository to the **ansible** language and ships two built-in profiles: **Qualimetry Ansible** and **Qualimetry Way**. The ITs do **not** assign a quality profile via the API; you must set it in SonarQube before running the ITs.

## Before running ITs

**Set the default Ansible quality profile to Qualimetry Way** (or assign Qualimetry Way to the IT projects) in SonarQube:

1. In SonarQube go to **Quality Profiles**.
2. For the **Ansible** language, set **Qualimetry Way** as the default profile,  
   **or** after the first IT run, assign **Qualimetry Way** to projects `ansible-its-compliant` and `ansible-its-noncompliant`.

If the default Ansible profile is not Qualimetry Way (or the projects are not assigned that profile), the IT projects will be analyzed with whatever profile is default and the IT assertions may not match.

## Running integration tests (Maven)

From the **its/** directory, with a running SonarQube server that has the plugin installed and Qualimetry Way set as above:

```bash
mvn verify -Pits -Dsonar.host.url=YOUR_SONAR_URL -Dsonar.token=YOUR_TOKEN
```

The tests create the two projects via the API, run analysis on `projects/noncompliant` and `projects/compliant`, wait for completion, then assert issue counts and rule-specific issues. The noncompliant project is a set of **minimal violation samples** (small files, each triggering one or a few rules). Compliant is a single coherent playbook. Asserted rules include: qa-spaces-not-tabs, qa-valid-yaml, qa-strip-trailing-whitespace, qa-task-has-name, qa-task-name-first, qa-delegate-to-localhost.

- To use a specific Sonar scanner executable (e.g. not on PATH), set **`-Dsonar.scanner.bin`** to the full path to `sonar-scanner` or `sonar-scanner.bat`.
- If `sonar.scanner.bin` is not set and the project dir has a `pom.xml`, the IT uses **Maven sonar:sonar**; otherwise **sonar-scanner** from PATH.

## Pushing projects manually

Analyses can also be pushed with a Sonar scanner or Maven using the same project keys and quality profile. For **noncompliant** to show issues, the SonarQube project must use the **Qualimetry Way** (or Qualimetry Ansible) quality profile for the Ansible language.
