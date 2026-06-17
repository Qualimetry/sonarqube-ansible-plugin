# Remove duplicate task definitions

`qa-unique-tasks` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Duplicate tasks (same name and module) in a play are redundant and can cause double execution or confusion. Consolidate them into a single task (e.g. with a list argument) or give them distinct names and purposes.

### Ask yourself whether

- You have multiple tasks with the same name and module that could be merged.
- You intended to run one task but copied it by mistake.

### Recommended practices

- Use a single task with a list (e.g. name: [foo, bar]) when the operation is the same. Use distinct names when the tasks are logically different.

## Noncompliant code example

```yaml
- hosts: all
  tasks:
    - name: Install package
      apt:
        name: foo
    - name: Install package
      apt:
        name: bar
```

## Compliant solution

```yaml
- hosts: all
  tasks:
    - name: Install packages
      apt:
        name:
          - foo
          - bar
```

## See also

- [Ansible - Intro to playbooks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_intro.html)
