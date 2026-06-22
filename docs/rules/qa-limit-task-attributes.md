# Reduce task or playbook complexity

`qa-limit-task-attributes` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Tasks with too many attributes (keys) are hard to read and maintain. A configurable limit encourages splitting into multiple tasks or using variables and blocks so each task has a clear, focused purpose.

### Ask yourself whether

- This task has many keys (name, module, when, loop, vars, register, etc.) that could be simplified.
- You want to improve readability and reuse.

### Recommended practices

- Extract repeated or complex values into variables. Use block for grouping instead of repeating the same attributes. Set a project-specific attribute limit.

## Noncompliant code example

```yaml
- name: Complex task
  copy:
    src: a
    dest: /tmp/a
  when: x
  loop: items
  vars:
    a: 1
  register: out
  changed_when: true
  failed_when: false
  # ... many more keys
```

## Compliant solution

```yaml
- name: Copy file
  copy:
    src: a
    dest: /tmp/a
  when: deploy_enabled
```

## See also

- [Ansible - Intro to playbooks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_intro.html)
