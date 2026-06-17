# Use .yml or .yaml for playbooks

`qa-playbook-yml-extension` &middot; Formatting &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Playbook files should use a `.yml` or `.yaml` extension so tools, editors, and CI can identify them as YAML and apply the right parsing and syntax highlighting. Extensionless or non-standard names can be skipped or misprocessed.

Ansible discovers playbooks by path; using a standard extension is a convention that improves tooling support and makes the intent of the file clear to humans and automation.

### Ask yourself whether

- Your playbook file has no extension or uses a custom one (e.g. `.play`).
- CI, editors, or other tools rely on file extension to detect YAML.

### Recommended practices

- Name playbook files with `.yml` or `.yaml` (e.g. `site.yml`, `deploy.yaml`).
- Use the same convention across the project for consistency.

## Noncompliant code example

```yaml
# file: deploy (no extension)
- hosts: all
  tasks:
    - name: Run app
      command: /usr/bin/app
```

## Compliant solution

```yaml
# file: deploy.yml (or deploy.yaml)
- hosts: all
  tasks:
    - name: Run app
      command: /usr/bin/app
```

## See also

- [Ansible - Intro to playbooks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_intro.html)
