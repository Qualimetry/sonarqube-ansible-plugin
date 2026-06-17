# Use Ansible module instead of command

`qa-use-module-not-command` &middot; Security &middot; Code Smell &middot; severity MAJOR &middot; optional

Using an Ansible module instead of `command` or `shell` when a module exists gives you idempotency, clearer change reporting, and better portability. Raw commands (e.g. `cp`, `chown`) are not idempotent and can cause repeated "changed" or inconsistent state.

Modules like `copy`, `file`, `package`, and `user` are designed for declarative, repeatable tasks. They check current state and only report "changed" when something actually changes. Prefer them over wrapping system commands.

### Ask yourself whether

- An Ansible module can perform the same operation (copy, install, user management, etc.).
- You want the task to be idempotent and to report changes accurately.

### Recommended practices

- Search the module index for the operation (e.g. copy file, install package) and use that module.
- Use `command` or `shell` only when no module fits or when you need a one-off script.

## Noncompliant code example

```yaml
- name: Copy file
  command: cp /tmp/a.conf /etc/a.conf
```

## Compliant solution

```yaml
- name: Copy file
  copy:
    src: /tmp/a.conf
    dest: /etc/a.conf
```

## See also

- [Ansible - Module index](https://docs.ansible.com/ansible/latest/collections/index.html)
