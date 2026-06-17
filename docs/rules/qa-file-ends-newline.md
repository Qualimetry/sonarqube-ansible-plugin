# End file with newline

`qa-file-ends-newline` &middot; Formatting &middot; Code Smell &middot; severity INFO &middot; enabled in the default profile

Files that do not end with a newline can cause problems with some tools (e.g. concatenation, diff, or CI scripts that expect a trailing newline). POSIX defines a line as ending with a newline character.

Many style guides and linters require a final newline. Adding one is a small change that improves consistency and avoids tooling issues.

### Ask yourself whether

- Your editor is configured to not add a newline at end of file.
- Files are processed by scripts that assume a trailing newline.

### Recommended practices

- Enable "Insert final newline" (or equivalent) in your editor.
- Run a formatter or linter that enforces this rule before committing.

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

- [POSIX - Definition of a line](https://pubs.opengroup.org/onlinepubs/9699919799/basedefs/V1_chap03.html#tag_03_206)
