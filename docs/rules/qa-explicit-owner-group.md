# Set owner and group explicitly

`qa-explicit-owner-group` &middot; Security &middot; Code Smell &middot; severity MAJOR &middot; enabled in the default profile

Using `same_owner` (or equivalent) copies ownership from the source, which may not be what you want on the target and can be inconsistent across environments. Setting `owner` and `group` explicitly makes the intended state clear and repeatable.

Explicit owner and group improve auditability and avoid surprises when source files have different ownership on different systems. They also align with declarative, idempotent configuration.

### Ask yourself whether

- You use same_owner or rely on default ownership.
- You want consistent, documented ownership on the target.

### Recommended practices

- Set `owner` and `group` explicitly on copy, file, or template tasks. Use variables (e.g. from role defaults) when the same value is used in many places.

## Noncompliant code example

```yaml
- name: Copy with same_owner
  copy:
    src: a
    dest: /tmp/a
    same_owner: true
```

## Compliant solution

```yaml
- name: Copy with explicit owner
  copy:
    src: a
    dest: /tmp/a
    owner: root
    group: root
```

## See also

- [Ansible - copy module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/copy_module.html)
