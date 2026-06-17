# Prefer length or presence over empty string compare

`qa-check-length-not-empty` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Comparing a variable to an empty string (e.g. when: my_var == "") is fragile: the variable might be undefined or a different type. Prefer checking length or presence (e.g. when: my_var | length > 0 or when: my_var is defined and my_var) so the condition is clear and safe.

### Ask yourself whether

- You use == "" or != "" in when conditions or elsewhere.
- The variable might be undefined or a list.

### Recommended practices

- Use when: my_var | length > 0 for non-empty string, or when: my_var is defined and my_var for truthy check. Use when: my_list | length > 0 for lists.

## Noncompliant code example

```yaml
- name: Run when not empty
  debug:
    msg: "{{ my_var }}"
  when: my_var != ""
```

## Compliant solution

```yaml
- name: Run when not empty
  debug:
    msg: "{{ my_var }}"
  when: my_var is defined and my_var | length > 0
```

## See also

- [Ansible - Conditionals](https://docs.ansible.com/ansible/latest/user_guide/playbooks_conditionals.html)
