# Limit number of tasks per play

`qa-limit-tasks-per-play` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Plays with too many tasks are hard to maintain, review, and debug. A configurable limit encourages splitting into multiple plays or roles so each unit has a clear purpose and manageable size.

### Ask yourself whether

- This play has dozens of tasks that could be grouped into roles or separate plays.
- You want to improve readability and reuse.

### Recommended practices

- Extract related tasks into roles. Use multiple plays (or import_playbook) to split by host group or concern.
- Set a project-specific limit (e.g. 20–50 tasks per play) and enforce it via this rule.

## Noncompliant code example

```yaml
# A play with 50+ tasks in one block
- hosts: all
  tasks:
    - name: Task 1
      debug: {}
    # ... 50+ tasks
```

## Compliant solution

```yaml
- hosts: all
  roles:
    - common
    - app
- hosts: db
  tasks:
    - name: Configure DB
      template: { src: a.conf.j2, dest: /etc/a.conf }
```

## See also

- [Ansible - Roles](https://docs.ansible.com/ansible/latest/user_guide/playbooks_reuse_roles.html)
