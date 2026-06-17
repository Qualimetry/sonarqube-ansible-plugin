# Task must have a name

`qa-task-has-name` &middot; Naming &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Every task should have a name so that playbook output and logs are readable. Unnamed tasks show only the module name, which makes debugging and auditing harder.

The name field is displayed when Ansible runs the task. Without it, output is less informative and it is harder to find the failing task in a long playbook.

### Ask yourself whether

- Someone will need to read playbook output or logs to understand what ran.
- You want to support debugging and compliance auditing.

### Recommended practices

- Add a short, descriptive name to every task. Place name first in the task.

## Noncompliant code example

```yaml
- hosts: all
  tasks:
    - ping:
```

## Compliant solution

```yaml
- hosts: all
  tasks:
    - name: Ping host
      ping:
```

## See also

- [Ansible - Intro to playbooks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_intro.html)
