# Limit number of plays per playbook

`qa-limit-plays` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Playbooks with many plays in one file become hard to navigate and maintain. Splitting into multiple playbook files (and using import_playbook) keeps each file focused and makes it easier to run subsets.

### Ask yourself whether

- This playbook has many plays that could be split by environment or concern.
- You want a main entry point that imports smaller playbooks.

### Recommended practices

- Use import_playbook to include other playbook files. Keep the main file (e.g. site.yml) to a few plays and import the rest.

## Noncompliant code example

```yaml
# 10+ plays in one file
- hosts: a
  tasks: []
- hosts: b
  tasks: []
```

## Compliant solution

```yaml
# site.yml - main entry
- hosts: all
  tasks: []
- import_playbook: db.yml
- import_playbook: app.yml
```

## See also

- [Ansible - import_playbook](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/import_playbook_module.html)
