# Remove or use unused variables

`qa-remove-unused-vars` &middot; Reliability &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Variables that are defined but never used add noise and can cause confusion about what is actually in use. Remove them or use them so the playbook stays clear and maintainable.

### Ask yourself whether

- You have leftover variables from refactoring or copy-paste.
- You want to keep the playbook free of dead code.

### Recommended practices

- Delete or comment out unused variables. If a variable is optional (e.g. for override), document it and ensure it is referenced when set.

## Noncompliant code example

```yaml
vars:
  unused_var: value
  used_var: other
tasks:
  - debug:
      var: used_var
```

## Compliant solution

```yaml
vars:
  used_var: other
tasks:
  - debug:
      var: used_var
```

## See also

- [Ansible - Variables](https://docs.ansible.com/ansible/latest/user_guide/playbooks_variables.html)
