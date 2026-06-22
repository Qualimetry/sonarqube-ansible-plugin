# Restrict to ansible.builtin modules

`qa-builtin-modules-only` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; optional

Restricting to ansible.builtin modules reduces dependency on external collections and keeps playbooks portable. Use built-in modules when they can do the job; use other collections only when needed.

### Ask yourself whether

- You use modules from community or other collections where a builtin exists.

### Recommended practices

- Prefer ansible.builtin.* when the functionality exists. Use FQCN and document when you require a non-builtin collection.

## Noncompliant code example

```yaml
- name: Use community module
  community.general.aws_s3:
    bucket: my-bucket
```

## Compliant solution

```yaml
- name: Use builtin
  ansible.builtin.copy:
    src: a
    dest: /tmp/a
```

## See also

- [Ansible - Builtin collection](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/index.html)
