# Follow fact naming conventions

`qa-fact-name-format` &middot; Naming &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Fact names (set_fact, or facts from tasks) should follow a consistent convention (e.g. lowercase with underscores) so they are predictable and avoid clashes with reserved names.

### Ask yourself whether

- You use mixed case or hyphens in fact names.

### Recommended practices

- Use lowercase and underscores for fact names (e.g. app_installed, config_path).

## Noncompliant code example

```yaml
- set_fact:
    MyFact: value
```

## Compliant solution

```yaml
- set_fact:
    my_fact: value
```

## See also

- [Ansible - Variables](https://docs.ansible.com/ansible/latest/user_guide/playbooks_variables.html)
