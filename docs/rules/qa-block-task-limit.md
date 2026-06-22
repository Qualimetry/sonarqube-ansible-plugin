# Limit number of tasks in block

`qa-block-task-limit` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Blocks with too many tasks become hard to read and maintain. A configurable limit encourages splitting into multiple blocks or roles so each block has a clear, focused purpose.

### Ask yourself whether

- This block has many tasks that could be split or moved to a role.

### Recommended practices

- Keep blocks small. Extract related tasks into a role or use multiple blocks with descriptive names.

## Noncompliant code example

```yaml
- block:
    - name: Task 1
      debug: {}
    # ... 20+ tasks
```

## Compliant solution

```yaml
- block:
    - name: Install
      package: { name: foo }
    - name: Configure
      template: { src: a.j2, dest: /etc/a }
```

## See also

- [Ansible - Blocks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_blocks.html)
