# Define or pass undefined variables

`qa-define-referenced-vars` &middot; Reliability &middot; Bug &middot; severity MAJOR &middot; enabled in the default profile

Variables that are referenced but not defined (in vars, vars_files, role defaults, or extra vars) cause runtime errors when the task runs. Define them or pass them (e.g. -e) so the playbook executes correctly.

### Ask yourself whether

- You reference a variable that might not be set in all contexts (e.g. host vars, group vars).
- You added a new reference but did not add a default or pass the value.

### Recommended practices

- Define variables in role defaults, group_vars, or host_vars. Use default() in templates or when for optional values. Pass required vars via -e or inventory.

## Noncompliant code example

```yaml
- name: Use undefined var
  debug:
    msg: "{{ my_undefined_var }}"
```

## Compliant solution

```yaml
- name: Use var with default
  debug:
    msg: "{{ my_var | default('ok') }}"
# Or define in vars: or role defaults
```

## See also

- [Ansible - Variables](https://docs.ansible.com/ansible/latest/user_guide/playbooks_variables.html)
