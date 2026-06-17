# Use delegate_to localhost instead of local_action

`qa-delegate-to-localhost` &middot; Best Practices &middot; Code Smell &middot; severity MAJOR &middot; enabled in the default profile

The `local_action` shorthand is deprecated. Ansible recommends using the normal module syntax with `delegate_to: localhost` so that task structure is consistent and future-proof.

`local_action` was a convenience for running a module on the control node. The same effect is achieved with `delegate_to: localhost`, which is the supported pattern in current and future Ansible versions.

### Ask yourself whether

- You are using `local_action` in any task.
- You want to avoid deprecated syntax that may be removed later.

### Recommended practices

- Replace `local_action: module_name` with the module name as the key and `delegate_to: localhost`.
- Use the args form for the module when applicable.

## Noncompliant code example

```yaml
- name: Run locally
  local_action: ping
```

## Compliant solution

```yaml
- name: Run locally
  ping:
  delegate_to: localhost
```

## See also

- [Ansible - Delegation](https://docs.ansible.com/ansible/latest/user_guide/playbooks_delegation.html)
