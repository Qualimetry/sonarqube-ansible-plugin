# Valid YAML structure required

`qa-valid-yaml` &middot; Reliability &middot; Bug &middot; severity MAJOR &middot; enabled in the default profile

Playbooks and task files must be valid YAML. Invalid syntax causes the parser to fail and prevents Ansible from loading the file. Errors can be subtle (wrong indentation, unclosed brackets) and may only appear at runtime.

YAML is indentation-sensitive and requires consistent structure. Fix syntax errors reported here before execution so the playbook loads correctly.

### Ask yourself whether

- The file has mixed tabs and spaces, or inconsistent indentation.
- There are unclosed quotes, brackets, or list items.

### Recommended practices

- Use spaces only for indentation; keep 2 or 4 spaces consistently.
- Quote strings that contain colons or hashes. Validate with ansible-playbook --syntax-check.

## Noncompliant code example

```yaml
- hosts: all
  tasks:
    - name: Bad
      copy: [unclosed
```

## Compliant solution

```yaml
- hosts: all
  tasks:
    - name: Copy file
      copy:
        src: a
        dest: /tmp
```

## See also

- [Ansible - YAML Syntax](https://docs.ansible.com/ansible/latest/reference_appendices/YAMLSyntax.html)
