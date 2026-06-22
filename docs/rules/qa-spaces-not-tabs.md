# Disallow tab characters

`qa-spaces-not-tabs` &middot; Formatting &middot; Code Smell &middot; severity INFO &middot; enabled in the default profile

Using tab characters for indentation causes inconsistent behavior across editors and systems. YAML is space-sensitive; some parsers treat tabs differently, which can lead to parse errors or subtle bugs.

Spaces are the standard for YAML indentation. Using only spaces (typically 2 or 4 per level) keeps playbooks portable and avoids "tab vs space" issues in version control and CI.

### Ask yourself whether

- Your editor inserts tabs when you press Tab.
- The file was edited on a system or editor with different tab settings.
- You want consistent display and parsing in all environments.

### Recommended practices

- Configure your editor to insert spaces when pressing Tab (e.g. "Insert spaces" in VS Code).
- Use a YAML or Ansible formatter/linter that enforces spaces.
- Search for tab characters (`\t`) and replace with the same number of spaces as your indent width.

## Noncompliant code example

```yaml
- hosts: all
	tasks:    # tab used
```

## Compliant solution

```yaml
- hosts: all
  tasks:
```

## See also

- [YAML - Indentation](https://yaml.org/spec/1.2/spec.html#id2777534)
