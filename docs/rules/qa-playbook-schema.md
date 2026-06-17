# Fix schema validation

`qa-playbook-schema` &middot; Reliability &middot; Bug &middot; severity MAJOR &middot; enabled in the default profile

The playbook or role file does not match the expected YAML/Ansible structure (e.g. missing hosts, invalid key under tasks). Fix the structure so the file is valid and can be executed.

### Ask yourself whether

- You have a play with no hosts, or tasks with invalid keys.

### Recommended practices

- Ensure each play has hosts and that task entries have a module or include. Follow Ansible playbook structure (plays, tasks, handlers).

## Noncompliant code example

```yaml
- tasks:
    - name: Bad
      debug: {}  # play has no hosts
```

## Compliant solution

```yaml
- hosts: all
  tasks:
    - name: Ok
      debug: { msg: ok }
```

## See also

- [Ansible - Intro to playbooks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_intro.html)
