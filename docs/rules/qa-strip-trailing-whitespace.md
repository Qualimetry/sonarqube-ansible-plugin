# Remove trailing whitespace

`qa-strip-trailing-whitespace` &middot; Formatting &middot; Code Smell &middot; severity INFO &middot; enabled in the default profile

Trailing spaces or tabs at the end of lines add no meaning and clutter version control diffs. They can also cause inconsistent behavior in some contexts (e.g. copied strings, comparisons).

Removing trailing whitespace keeps playbooks clean and makes diffs easier to read. Most editors can trim whitespace on save or on demand.

### Ask yourself whether

- You often paste or type at the end of lines without clearing extra spaces.
- Your team wants clean diffs and consistent formatting.

### Recommended practices

- Enable "Trim trailing whitespace" (or equivalent) in your editor.
- Use a formatter or pre-commit hook that strips trailing whitespace.

## Noncompliant code example

```yaml
- hosts: all 
  tasks: []
```

## Compliant solution

```yaml
- hosts: all
  tasks: []
```

## See also

- Editor settings for "trim trailing whitespace" or "strip whitespace"
