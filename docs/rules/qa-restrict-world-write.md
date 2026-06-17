# Avoid world-writable permissions

`qa-restrict-world-write` &middot; Security &middot; Code Smell &middot; severity CRITICAL &middot; optional

World-writable file modes (e.g. 0666, 0777, or modes that include write for "other") allow any user on the system to modify the file. That can lead to tampering, privilege escalation, or execution of malicious code.

Restrict file permissions to the owner and optionally the group. Use modes such as 0644 or 0755 unless there is a documented reason for broader access.

### Ask yourself whether

- The file contains configuration, data, or code that should not be writable by all users.
- The mode includes write (2) or execute (1) for "other".

### Recommended secure coding practices

- Use 0644 for regular files and 0755 for executables. Avoid 0666, 0777, and similar world-writable modes.
- Set owner and group explicitly when possible.

## Noncompliant code example

```yaml
- name: Create config
  copy:
    src: app.conf
    dest: /etc/app.conf
    mode: '0666'
```

## Compliant solution

```yaml
- name: Create config
  copy:
    src: app.conf
    dest: /etc/app.conf
    mode: '0644'
```

## See also

- [CWE-732 - Incorrect Permission Assignment for Critical Resource](https://cwe.mitre.org/data/definitions/732.html)
