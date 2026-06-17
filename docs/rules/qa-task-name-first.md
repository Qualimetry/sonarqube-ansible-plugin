# Task name before module key

`qa-task-name-first` &middot; Naming &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Placing the task `name` before the module key improves readability and matches Ansible best practices. When `name` appears first, run output and logs show a clear label before the module name.

Ansible allows `name` in any order, but putting it first is the recommended style. It makes playbooks easier to scan and aligns with most examples and style guides.

### Ask yourself whether

- You want playbook output to show a human-readable task name first.
- Your style guide or team convention requires `name` before the module.

### Recommended practices

- Always put `name` as the first key in each task.
- Use a linter or formatter that enforces key order.

## Noncompliant code example

```yaml
- copy:
    src: a
    dest: /tmp
  name: Copy file
```

## Compliant solution

```yaml
- name: Copy file
  copy:
    src: a
    dest: /tmp
```

## See also

- [Ansible - Intro to playbooks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_intro.html)
