# Fix Jinja2 template usage

`qa-jinja-format` &middot; Naming &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Jinja2 expressions (e.g. {{ var }}, {% if %}) must be well-formed. Invalid or inconsistent spacing can change how the expression is parsed or rendered. Fix the expression so it is valid and matches project style (e.g. spacing around {{ }}).

### Ask yourself whether

- You have {{ var }} or {% %} with wrong spacing or invalid syntax.

### Recommended practices

- Use consistent spacing (e.g. {{ var }}). Avoid redundant Jinja in when/loop (use bare variables when appropriate).

## Noncompliant code example

```yaml
msg: "{{var}}"  # or invalid {{ }}
```

## Compliant solution

```yaml
msg: "{{ var }}"
```

## See also

- [Jinja2 documentation](https://jinja.palletsprojects.com/)
