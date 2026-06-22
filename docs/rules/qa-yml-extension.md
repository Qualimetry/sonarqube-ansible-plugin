# Follow file naming conventions

`qa-yml-extension` &middot; Formatting &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

YAML files (playbooks, role tasks, vars) should use .yml or .yaml so tools and editors detect them as YAML and apply correct parsing and syntax highlighting.

### Ask yourself whether

- Your file has no extension or a non-YAML extension (e.g. .txt).

### Recommended practices

- Use .yml or .yaml for all Ansible YAML files. Prefer one convention (e.g. .yml) across the project.

## Noncompliant code example

```yaml
# File named playbook.txt or deploy
```

## Compliant solution

```yaml
# File named playbook.yml or main.yaml
```

## See also

- [Ansible - Intro to playbooks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_intro.html)
