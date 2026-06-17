# Do not log sensitive data

`qa-no-log-secrets` &middot; Security &middot; Code Smell &middot; severity BLOCKER &middot; optional

Logging task output that contains passwords, API keys, or other secrets exposes them to anyone with access to Ansible output, CI logs, or syslog. That can lead to credential theft and privilege escalation.

The `no_log` task parameter controls whether the task's arguments and return values are recorded. When `no_log` is omitted or `false`, Ansible may log module arguments and results that include sensitive data.

Anyone who can read job logs or playbook output may reuse those credentials. You should set `no_log: true` for any task that uses or returns secrets.

### Ask yourself whether

- This task uses a password, token, API key, or other secret.
- Playbook or job output is visible to people who should not see those values.
- Logs are stored in a system that is not fully restricted (e.g. shared CI).

### Recommended secure coding practices

- Set `no_log: true` on every task that passes or returns sensitive data.
- Use Ansible Vault (or a secrets manager) for variables that hold secrets.
- Avoid passing secrets on the command line; use module parameters or environment variables with `no_log: true`.

## Noncompliant code example

```yaml
- name: Set password
  user:
    name: foo
    password: "{{ secret }}"
```

## Compliant solution

```yaml
- name: Set password
  user:
    name: foo
    password: "{{ secret }}"
  no_log: true
```

## See also

- [Ansible - Special Variables (no_log)](https://docs.ansible.com/ansible/latest/reference_appendices/special_variables.html)
- [CWE-532 - Insertion of Sensitive Information into Log File](https://cwe.mitre.org/data/definitions/532.html)
