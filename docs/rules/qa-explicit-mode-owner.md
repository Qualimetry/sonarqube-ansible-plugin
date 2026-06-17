# Avoid implicit file mode or ownership

`qa-explicit-mode-owner` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; optional

When creating or copying files, implicit default mode or ownership can differ across systems. Set mode and owner/group explicitly so the result is consistent and auditable.

### Ask yourself whether

- You use copy, file, or template without setting mode or owner/group where security or consistency matters.

### Recommended practices

- Set mode (e.g. 0644, 0755) and owner/group on file operations when the target state is important. Use variables for reuse.

## Noncompliant code example

```yaml
- name: Copy file
  copy:
    src: app.conf
    dest: /etc/app.conf
```

## Compliant solution

```yaml
- name: Copy file
  copy:
    src: app.conf
    dest: /etc/app.conf
    mode: 0644
    owner: root
    group: root
```

## See also

- [Ansible - copy module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/copy_module.html)
