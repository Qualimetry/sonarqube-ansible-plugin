# Prefer import_tasks over include_tasks when appropriate

`qa-import-versus-include` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

import_tasks is static (parsed at playbook parse time); include_tasks is dynamic (e.g. can use loop). Use import_tasks when the file is fixed so Ansible can optimize and catch errors earlier. Use include_tasks when the path or inclusion is conditional or looped.

### Ask yourself whether

- You include a task file with a fixed path and no loop.

### Recommended practices

- Prefer import_tasks for static includes. Use include_tasks when you need with_items, when, or a variable path.

## Noncompliant code example

```yaml
- name: Include static file
  include_tasks: tasks/setup.yml
```

## Compliant solution

```yaml
- name: Import static file
  import_tasks: tasks/setup.yml
```

## See also

- [Ansible - import_tasks vs include_tasks](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/import_tasks_module.html)
