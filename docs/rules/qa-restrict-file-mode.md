# Avoid risky file permissions

`qa-restrict-file-mode` &middot; Security &middot; Code Smell &middot; severity CRITICAL &middot; optional

Overly permissive file modes (e.g. 0777 or 0666) allow any user to read, write, or execute files. That can lead to privilege escalation, data tampering, or execution of malicious code.

When you set `mode` on `copy`, `file`, or `template`, avoid world-writable or world-executable bits unless there is a documented reason. Restrict files to the owner and optionally group.

### Ask yourself whether

- This file contains secrets, binaries, or configuration that should not be writable by others.
- The mode includes 2 (write) or 1 (execute) for "other" (e.g. 0777, 0666).

### Recommended secure coding practices

- Use modes such as 0644 (file) or 0755 (executable) instead of 0777 or 0666.
- Set `owner` and `group` explicitly when possible.
- Document any exception where broad permissions are required.

## Noncompliant code example

```yaml
- name: Copy file
  copy:
    src: app.sh
    dest: /tmp/app.sh
    mode: 0777
```

## Compliant solution

```yaml
- name: Copy file
  copy:
    src: app.sh
    dest: /tmp/app.sh
    mode: 0755
```

## See also

- [CWE-732 - Incorrect Permission Assignment for Critical Resource](https://cwe.mitre.org/data/definitions/732.html)
- [Ansible - copy module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/copy_module.html)
