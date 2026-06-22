# Prefer block for grouping tasks

`qa-group-tasks-in-block` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Using block groups related tasks and allows a single rescue or always for the whole group. That improves readability and makes it easier to handle failures for a set of tasks (e.g. install and configure) in one place.

### Ask yourself whether

- You have a sequence of related tasks that share the same when, become, or error handling.

### Recommended practices

- Wrap related tasks in a block. Use rescue and always when you need to handle failures or cleanup for the group.

## Noncompliant code example

```yaml
- name: Task 1
  debug: { msg: a }
- name: Task 2
  debug: { msg: b }
- name: Task 3
  debug: { msg: c }
```

## Compliant solution

```yaml
- block:
    - name: Task 1
      debug: { msg: a }
    - name: Task 2
      debug: { msg: b }
    - name: Task 3
      debug: { msg: c }
  rescue:
    - name: On failure
      debug: { msg: failed }
```

## See also

- [Ansible - Blocks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_blocks.html)
