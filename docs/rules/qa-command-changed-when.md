# Avoid changed_when with only static values

`qa-command-changed-when` &middot; Best Practices &middot; Bug &middot; severity MAJOR &middot; enabled in the default profile

For `command` and `shell` tasks, Ansible reports "changed" by default whenever the task runs. If the command is not idempotent, that can mislead users and automation. Use `changed_when` to reflect when a real change occurred.

Commands that only read state (e.g. echo, checks) should use `changed_when: false`. Commands that may change state should use a `changed_when` condition based on output or return code so that "changed" is accurate.

### Ask yourself whether

- This command or shell task is idempotent or only reports status.
- You want "changed" to mean that something actually changed on the host.

### Recommended practices

- Use `changed_when: false` for read-only or diagnostic commands.
- Use `changed_when` with a condition (e.g. on stdout or rc) when the command can change state.
- Prefer Ansible modules over command/shell when possible for accurate change reporting.

## Noncompliant code example

```yaml
- name: Run command
  command:
    cmd: echo hello
```

## Compliant solution

```yaml
- name: Run command
  command:
    cmd: echo hello
  changed_when: false
```

## See also

- [Ansible - Error handling (changed_when)](https://docs.ansible.com/ansible/latest/user_guide/playbooks_error_handling.html#defining-changed)
