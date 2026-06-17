# Use bare variable in when/loop not {{ var }}

`qa-bare-var-in-condition` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

In when or loop, use the variable name alone (bare variable) rather than {{ var }} when you only need a simple reference. Ansible expands variables automatically; the extra Jinja delimiters are redundant and can change how the value is interpreted (e.g. string vs boolean).

### Ask yourself whether

- Your when or loop uses a single variable with no expression (e.g. no comparison, no filter).
- You want consistent, idiomatic Ansible style.

### Recommended practices

- Use `when: my_flag` instead of `when: "{{ my_flag }}"`. Use `loop: my_list` instead of `loop: "{{ my_list }}"` when it is a simple variable reference.

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
