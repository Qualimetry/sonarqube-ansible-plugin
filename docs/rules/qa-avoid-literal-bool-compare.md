# Prefer when: var over when: var == "yes"

`qa-avoid-literal-bool-compare` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Use when: var rather than when: var == "yes" (or "true", "no", "false"). Ansible treats truthy/falsy values in conditions; literal string comparison is redundant and can be wrong if the variable is already a boolean.

### Ask yourself whether

- You are comparing a variable to "yes", "true", "no", or "false" in a when condition.
- The variable is set from a role default or fact that may already be boolean.

### Recommended practices

- Use `when: my_feature` for a positive check and `when: not my_feature` for negative. Use explicit comparison only when you must match a specific string value.

## Noncompliant code example

```yaml
- name: Run when enabled
  debug:
    msg: "running"
  when: my_feature == "yes"
```

## Compliant solution

```yaml
- name: Run when enabled
  debug:
    msg: "running"
  when: my_feature
```

## See also

- [Ansible - Conditionals](https://docs.ansible.com/ansible/latest/user_guide/playbooks_conditionals.html)
