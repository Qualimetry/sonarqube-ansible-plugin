# Avoid risky octal modes

`qa-numeric-file-mode` &middot; Security &middot; Code Smell &middot; severity CRITICAL &middot; optional

Using a string for the file `mode` (e.g. `"0777"`) can be misinterpreted: some versions or modules may treat it as a decimal or handle leading zeros differently. Use a numeric literal or an explicit format to avoid confusion and security mistakes.

In YAML, `0777` (unquoted) is parsed as octal. Quoting it as `"0777"` makes it a string and behavior can vary. Consistently using numeric octal or a documented string format reduces errors.

### Ask yourself whether

- You use a quoted string for `mode` (e.g. `"0755"`).
- You want to avoid ambiguity between octal and decimal interpretation.

### Recommended practices

- Use numeric mode (e.g. `0755`) so YAML parses it as octal.
- If you must use a string, ensure the module documentation supports it and use a consistent format.

## Noncompliant code example

```yaml
- name: Copy file
  copy:
    src: a
    dest: /tmp/a
    mode: "0777"
```

## Compliant solution

```yaml
- name: Copy file
  copy:
    src: a
    dest: /tmp/a
    mode: 0755
```

## See also

- [Ansible - copy module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/copy_module.html)
