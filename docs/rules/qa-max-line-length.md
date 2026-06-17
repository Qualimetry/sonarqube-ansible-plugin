# Limit line length

`qa-max-line-length` &middot; Formatting &middot; Code Smell &middot; severity INFO &middot; enabled in the default profile

Very long lines are hard to read and review in standard terminals and editors. They can also make diffs noisier and reduce the effectiveness of side-by-side review.

A maximum line length (e.g. 160 characters, configurable) encourages breaking long lines or extracting values. Shorter lines improve readability and tool compatibility.

### Ask yourself whether

- Lines exceed the width of your editor or terminal without wrapping.
- Long task names or module arguments could be shortened or split.

### Recommended practices

- Keep lines under a reasonable limit (e.g. 120–160 characters).
- Use YAML multiline syntax (`|` or `>`) for long strings.
- Break long lists or arguments across lines where it improves clarity.

## Noncompliant code example

```yaml
- name: A very long task name or line that exceeds the recommended maximum line length and makes the playbook harder to read and review in standard terminals and editors
  debug:
    msg: "ok"
```

## Compliant solution

```yaml
- name: Short task name
  debug:
    msg: "ok"
```

## See also

- Project or team style guide for line length
