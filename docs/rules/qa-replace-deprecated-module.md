# Avoid deprecated module usage

`qa-replace-deprecated-module` &middot; Deprecation &middot; Code Smell &middot; severity MAJOR &middot; enabled in the default profile

Deprecated Ansible modules may be removed in a future release. Using them risks breakage when you upgrade. Replace them with the recommended alternatives so playbooks remain compatible.

Ansible deprecates modules when a better or consolidated alternative exists (e.g. `include` vs `import_playbook` / `include_tasks`). The documentation for deprecated modules usually states the replacement.

### Ask yourself whether

- You use modules that appear as deprecated in the Ansible documentation.
- You want playbooks to work with future Ansible versions.

### Recommended practices

- Check module docs for deprecation notices and use the suggested replacement.
- For `include`: use `import_playbook` for playbooks and `include_tasks` or `import_tasks` for task files.
- Run playbooks with the latest Ansible and fix deprecation warnings.

## Noncompliant code example

```yaml
- hosts: all
  tasks:
    - name: Include playbook
      include: other.yml
```

## Compliant solution

```yaml
- hosts: all
  tasks:
    - name: Import playbook
      import_playbook: other.yml
```

## See also

- [Ansible - Porting guides](https://docs.ansible.com/ansible/latest/porting_guides/porting_guide_2.0.html)
