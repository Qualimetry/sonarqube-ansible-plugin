# Use consistent indentation

`qa-even-spaces-indent` &middot; Formatting &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Inconsistent indentation (e.g. mixing 2 and 4 spaces, or using an odd number of spaces) can cause YAML parse errors or subtle structural mistakes. YAML uses indentation to denote nesting; it must be consistent within a file.

Ansible and YAML parsers expect the same indent width throughout. Mixing widths or using odd values (e.g. 3 spaces) is error-prone and can break parsing or produce unexpected structure.

### Ask yourself whether

- Different parts of the file use different indent widths.
- You use an odd number of spaces (e.g. 3) for indentation.

### Recommended practices

- Use either 2 or 4 spaces for each indent level, consistently.
- Configure your editor to use a fixed indent size and avoid mixing tabs and spaces.
- Use a formatter that normalizes indentation.

## Noncompliant code example

```yaml
- hosts: all
   tasks:   # odd number of spaces
```

## Compliant solution

```yaml
- hosts: all
  tasks:
    - name: Ping
      ping:
```

## See also

- [YAML - Indentation](https://yaml.org/spec/1.2/spec.html#id2777534)
