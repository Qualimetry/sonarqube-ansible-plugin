# Avoid prompting for input

`qa-no-vars-prompt` &middot; Security &middot; Code Smell &middot; severity CRITICAL &middot; enabled in the default profile

Tasks that prompt for user input (vars_prompt or modules that wait for stdin) block execution and fail or hang when playbooks run non-interactively (CI, cron). Use extra vars, environment variables, or a secrets manager instead.

### Ask yourself whether

- This playbook runs in CI or without a TTY.
- You need to pass secrets; use a secure, non-interactive method.

### Recommended practices

- Do not use vars_prompt for unattended runs. Use -e, environment variables, or vault files.

## Noncompliant code example

```yaml
- hosts: all
  vars_prompt:
    - name: db_password
      prompt: Database password
```

## Compliant solution

```yaml
- hosts: all
  tasks:
    - name: Use password from env
      set_fact:
        db_password: "{{ lookup('env', 'DB_PASSWORD') }}"
```

## See also

- [Ansible - Prompts](https://docs.ansible.com/ansible/latest/user_guide/playbooks_prompts.html)
