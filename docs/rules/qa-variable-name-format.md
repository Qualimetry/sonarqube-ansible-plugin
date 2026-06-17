# Follow variable naming conventions

`qa-variable-name-format` &middot; Naming &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Variable names should be lowercase with only letters, numbers, and underscores so they are consistent with Ansible and YAML conventions and avoid conflicts with reserved names.

### Ask yourself whether

- You use uppercase or hyphens in variable names.
- You want to align with common Ansible style (e.g. group_vars, role vars).

### Recommended practices

- Use lowercase and underscores (e.g. my_var, app_port). Avoid hyphens and mixed case.

## Noncompliant code example

```yaml
- set_fact:
    MyVar: value
```

## Compliant solution

```yaml
- set_fact:
    my_var: value
```

## See also

- [Ansible - Variables](https://docs.ansible.com/ansible/latest/user_guide/playbooks_variables.html)
