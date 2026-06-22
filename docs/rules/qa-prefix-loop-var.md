# Prefix loop variable names

`qa-prefix-loop-var` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

When using loop_control.loop_var, give the variable a prefix (e.g. item_) so it does not shadow the default item and is clearly a loop variable. That avoids confusion in nested loops and makes templates easier to read.

### Ask yourself whether

- You use loop_var with a name that could conflict with other variables (e.g. user, file).
- You have nested loops and need distinct loop variable names.

### Recommended practices

- Use a prefix like item_ (e.g. item_user, item_file) for loop_var so the variable is clearly from a loop.

## Noncompliant code example

```yaml
- name: Create users
  user:
    name: "{{ user }}"
  loop: "{{ users }}"
  loop_control:
    loop_var: user
```

## Compliant solution

```yaml
- name: Create users
  user:
    name: "{{ item_user }}"
  loop: "{{ users }}"
  loop_control:
    loop_var: item_user
```

## See also

- [Ansible - Loops](https://docs.ansible.com/ansible/latest/user_guide/playbooks_loops.html)
