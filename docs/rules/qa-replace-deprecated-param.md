# Avoid deprecated module parameters

`qa-replace-deprecated-param` &middot; Deprecation &middot; Code Smell &middot; severity MAJOR &middot; enabled in the default profile

Deprecated module parameters may be removed in future Ansible. Check module docs and use the recommended parameter so playbooks stay compatible.

### Ask yourself whether

- You use parameters marked deprecated in the module docs.

### Recommended practices

- Replace deprecated parameters with the suggested alternative from the docs.

## Noncompliant code example

```yaml

- name: Copy file
  copy:
    src: a.conf
    dest: /etc/a.conf
    force: yes
```

## Compliant solution

```yaml

- name: Copy file
  copy:
    src: a.conf
    dest: /etc/a.conf
    state: present
```
