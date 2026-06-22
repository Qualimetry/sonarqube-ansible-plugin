# Use defaults/ not vars/ for role defaults

`qa-role-defaults-dir` &middot; Roles &middot; Code Smell &middot; severity MINOR &middot; optional

Role default variables should live in defaults/main.yml so they can be overridden by inventory or playbook vars. Variables in vars/main.yml have higher precedence and are not intended as overridable defaults.

### Ask yourself whether

- You put default values (that callers might override) in vars/ instead of defaults/.

### Recommended practices

- Use defaults/main.yml for role default values. Use vars/main.yml for role-internal vars that should not be overridden.

## Noncompliant code example

```yaml
# vars/main.yml
app_port: 8080  # intended as default
```

## Compliant solution

```yaml
# defaults/main.yml
app_port: 8080
```

## See also

- [Ansible - Variable precedence](https://docs.ansible.com/ansible/latest/user_guide/playbooks_variables.html#variable-precedence)
