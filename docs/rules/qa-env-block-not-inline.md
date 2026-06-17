# Avoid inline environment variables

`qa-env-block-not-inline` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; optional

Setting environment variables inline (e.g. in the shell command string) is hard to read and can cause quoting issues. Use the task-level environment key (or env:) so the variables are clear and correctly passed to the module.

### Ask yourself whether

- You embed environment variables in a command string (e.g. FOO=bar mycmd).

### Recommended practices

- Use the environment keyword on the task with a dict of variable names and values. Use environment with block when multiple tasks share the same env.

## Noncompliant code example

```yaml
- name: Run with env
  command: FOO=bar /usr/bin/app
```

## Compliant solution

```yaml
- name: Run with env
  command: /usr/bin/app
  environment:
    FOO: bar
```

## See also

- [Ansible - Environment variables](https://docs.ansible.com/ansible/latest/user_guide/playbooks_environment.html)
