# Use fully qualified collection names

`qa-full-module-name` &middot; Naming &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Using fully qualified collection names (FQCN), e.g. ansible.builtin.copy instead of copy, makes it clear which module is used and avoids conflicts when multiple collections provide a module with the same short name.

Ansible 2.10 and later uses collections. Short names like copy resolve to a default (often ansible.builtin), but that can change with configuration. FQCNs are unambiguous and future-proof.

### Ask yourself whether

- You use short module names (copy, file) without a collection prefix.
- Your playbooks may run in environments where the default collection differs.

### Recommended practices

- Use FQCNs for built-in modules (ansible.builtin.copy) and for third-party collection modules.
- Use FQCNs in playbooks for clarity even if you set a default collection.

## Noncompliant code example

```yaml
- name: Copy file
  copy:
    src: a.conf
    dest: /etc/a.conf
```

## Compliant solution

```yaml
- name: Copy file
  ansible.builtin.copy:
    src: a.conf
    dest: /etc/a.conf
```

## See also

- [Ansible - Using collections](https://docs.ansible.com/ansible/latest/user_guide/collections_using.html)
