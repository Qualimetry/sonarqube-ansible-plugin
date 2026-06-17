# Avoid free-form command or shell

`qa-command-args-form` &middot; Security &middot; Code Smell &middot; severity MAJOR &middot; optional

Using the free-form style for `command` or `shell` (a single string after the module name) makes it harder to pass arguments safely and to see exactly what will run. It also encourages string concatenation and can lead to quoting or injection issues.

Ansible allows either `command: echo hello` (free-form) or the args form with a list or named arguments (e.g. `command:` then `cmd: echo hello` or `argv: ["echo", "hello"]`). The args form makes the boundary between the executable and its arguments explicit and avoids ambiguity when variables or special characters are involved.

Using the args form improves consistency, auditability, and safety. Prefer the args form for `command` and `shell` tasks.

### Ask yourself whether

- The task uses variables or user input in the command string (injection or quoting risk).
- You want playbook reviews and logs to show exactly which arguments are passed.
- Your style guide or automation standards require explicit argument lists.

### Recommended secure coding practices

- Use the args form: put the command and arguments under `cmd` (or `argv`) instead of a single free-form string.
- Prefer the `ansible.builtin.command` or `ansible.builtin.shell` FQCN and pass arguments as a list where possible to avoid shell parsing issues.
- When you need shell features (pipes, redirects), use `shell` with the args form and consider `args` (e.g. `executable: /bin/bash`) if required.

## Noncompliant code example

```yaml
- name: Run something
  command: echo hello
```

## Compliant solution

```yaml
- name: Run something
  command:
    cmd: echo hello
```

## See also

- [Ansible - command module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/command_module.html)
- [Ansible - shell module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/shell_module.html)
