# Fix syntax check failure

`qa-playbook-syntax-run` &middot; Reliability &middot; Bug &middot; severity MAJOR &middot; optional

ansible-playbook --syntax-check reports errors when the playbook has structural or syntax issues. Fix the reported error so the playbook can run.

### Ask yourself whether

- You run --syntax-check and get an error.

### Recommended practices

- Run ansible-playbook --syntax-check before committing. Fix the reported line and context (hosts, tasks, module names, YAML).

## Noncompliant code example

```yaml
# File that fails --syntax-check
```

## Compliant solution

```yaml
- hosts: all
  tasks:
    - name: Valid
      debug: { msg: ok }
```

## See also

- [Ansible - Intro to playbooks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_intro.html)
