# Apply become consistently

`qa-become-with-user` &middot; Security &middot; Code Smell &middot; severity MAJOR &middot; optional

When `become_user` is set, `become: true` (or `yes`) must also be set. Otherwise `become_user` has no effect because privilege escalation is not enabled.

Ansible uses `become` to enable switching user (e.g. via sudo) and `become_user` to specify the target user. If only `become_user` is set, the task may run as the wrong user and the intent is unclear.

### Ask yourself whether

- You set `become_user` to run a task as another user.
- You expect the task to actually run as that user.

### Recommended practices

- Whenever you set `become_user`, also set `become: true` (or `yes`).
- Prefer setting both at play level when all tasks need the same escalation.

## Noncompliant code example

```yaml
- name: Do something
  command: echo hi
  become_user: root
```

## Compliant solution

```yaml
- name: Do something
  command: echo hi
  become: true
  become_user: root
```

## See also

- [Ansible - Privilege escalation](https://docs.ansible.com/ansible/latest/user_guide/become.html)
