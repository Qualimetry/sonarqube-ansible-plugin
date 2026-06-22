# Use bare variable in when not {{ var }}

`qa-when-bare-variable` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

For a when condition that is only a single variable, use the bare variable name, not Jinja {{ var }}. Ansible expands variables in when automatically; using {{ }} can force string evaluation and is redundant.

### Ask yourself whether

- Your when condition is just a variable with no comparison or filter.
- You want the condition to be evaluated as a boolean, not as a string.

### Recommended practices

- Use `when: my_flag` instead of `when: "{{ my_flag }}"`. For expressions (e.g. `var == "yes"`), use the expression without wrapping the whole thing in {{ }} when not needed.

## Noncompliant code example

```yaml
- name: Run when flag is set
  debug:
    msg: ok
  when: "{{ my_flag }}"
```

## Compliant solution

```yaml
- name: Run when flag is set
  debug:
    msg: ok
  when: my_flag
```

## See also

- [Ansible FAQ - When to use {{ }}](https://docs.ansible.com/ansible/latest/reference_appendices/faq.html#when-should-i-use-var-and-when-var)
