# Prefer shell module over command for shell features

`qa-command-not-shell-when-possible` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; optional

Use command instead of shell when you do not need shell features (pipes, redirects, variable expansion). The command module runs the program directly without a shell, which is safer and avoids parsing or injection issues.

shell passes the task to a shell (e.g. /bin/sh). For simple invocations (one executable and arguments), command is the better choice.

### Ask yourself whether

- You use shell but do not need pipes, redirects, or shell variables.
- You want to reduce the risk of shell parsing or injection.

### Recommended practices

- Use command for simple runs. Use shell only when you need pipes, redirects, or other shell features.
- When using shell, prefer the args form.

## Noncompliant code example

```yaml
- name: Echo message
  shell: echo hello world
```

## Compliant solution

```yaml
- name: Echo message
  command:
    cmd: echo hello world
```

## See also

- [Ansible - command module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/command_module.html)
- [Ansible - shell module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/shell_module.html)
