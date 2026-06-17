# Follow role naming conventions

`qa-role-name-format` &middot; Naming &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Role names should follow a consistent format so they work correctly with Galaxy, `ansible-galaxy`, and role dependencies. Lowercase names with only alphanumeric characters and underscores (and optional FQCN) are the standard.

Names with dashes or mixed case can cause issues in some contexts. Using `role_name` or `namespace.role_name` keeps roles portable and easy to reference.

### Ask yourself whether

- Your role name contains dashes, spaces, or uppercase letters.
- You plan to publish the role to Galaxy or reference it from meta dependencies.

### Recommended practices

- Use lowercase letters, numbers, and underscores (e.g. `my_role`).
- For FQCN use the form `namespace.role_name` (e.g. `ansible.builtin` or your collection name).

## Noncompliant code example

```yaml
- hosts: all
  roles:
    - MyRole
    - role-with-dashes
```

## Compliant solution

```yaml
- hosts: all
  roles:
    - my_role
    - namespace.my_role
```

## See also

- [Ansible - Roles](https://docs.ansible.com/ansible/latest/user_guide/playbooks_reuse_roles.html)
- [Galaxy - Creating a role](https://galaxy.ansible.com/docs/contributing/creating_role.html)
