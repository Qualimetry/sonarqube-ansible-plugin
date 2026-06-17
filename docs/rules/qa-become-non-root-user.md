# Become user must not be root

`qa-become-non-root-user` &middot; Security &middot; Code Smell &middot; severity CRITICAL &middot; optional

Running tasks as root (become_user: root) increases the impact of mistakes or compromise. Use a dedicated non-root user for least privilege so that a successful attack or bug has limited scope.

When become_user is root, any vulnerability in the task or module can lead to full system compromise. A non-root user with only the required permissions reduces that risk.

### Ask yourself whether

- This task must run as root, or could run as a user with limited privileges.
- Your security or compliance policy requires avoiding root where possible.

### Recommended secure coding practices

- Create and use a dedicated user (e.g. appuser, svc_myapp) with only the permissions the task needs.
- Set become_user to that user instead of root. Use become: true and limit sudo or equivalent to specific commands if needed.

## Noncompliant code example

```yaml
- name: Run as root
  copy:
    src: app
    dest: /opt/app
  become: true
  become_user: root
```

## Compliant solution

```yaml
- name: Run as app user
  copy:
    src: app
    dest: /opt/app
  become: true
  become_user: appuser
```

## See also

- [Ansible - Privilege escalation](https://docs.ansible.com/ansible/latest/user_guide/become.html)
- [CWE-250 - Execution with Unnecessary Privileges](https://cwe.mitre.org/data/definitions/250.html)
