# Include required tags

`qa-play-has-tags` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Plays without tags cannot be selected with --tags or --skip-tags, which makes it harder to run a subset of the playbook (e.g. only "config" or "deploy"). Adding tags to plays improves flexibility and CI usage.

### Ask yourself whether

- You run this playbook with --tags or --skip-tags.
- You want to support partial runs (e.g. only install, only config).

### Recommended practices

- Add a tags list to each play with meaningful names (e.g. deploy, config, install). Use consistent tag names across playbooks.

## Noncompliant code example

```yaml
- hosts: all
  tasks:
    - name: Do something
      debug:
        msg: "ok"
```

## Compliant solution

```yaml
- hosts: all
  tags:
    - deploy
    - app
  tasks:
    - name: Do something
      debug:
        msg: "ok"
```

## See also

- [Ansible - Tags](https://docs.ansible.com/ansible/latest/user_guide/playbooks_tags.html)
