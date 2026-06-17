# Fix role or playbook load failure

`qa-includes-resolve` &middot; Reliability &middot; Bug &middot; severity MAJOR &middot; enabled in the default profile

Include or import tasks (include_tasks, import_tasks, import_playbook) reference file paths. If the path does not exist or is not inside the project, the playbook will fail at runtime. Resolve paths so they point to existing files within the project or document the expected layout.

### Ask yourself whether

- You use include_tasks, import_tasks, or import_playbook with a path that might be missing.
- Paths are relative to the playbook or role and the expected directory structure is in place.

### Recommended practices

- Use paths relative to the role (e.g. tasks/main.yml) or playbook directory. Ensure the referenced files exist and are committed. Use role_path for role-internal includes.

## Noncompliant code example

```yaml
- name: Include missing file
  include_tasks: tasks/nonexistent.yml
```

## Compliant solution

```yaml
- name: Include existing file
  include_tasks: tasks/setup.yml
```

## See also

- [Ansible - import_tasks](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/import_tasks_module.html)
