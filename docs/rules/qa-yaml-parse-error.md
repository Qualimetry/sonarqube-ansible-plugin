# Fix parser error

`qa-yaml-parse-error` &middot; Reliability &middot; Bug &middot; severity MAJOR &middot; enabled in the default profile

Parser errors (invalid YAML or Ansible structure) prevent the file from being loaded and analyzed. Fix the reported error (e.g. indentation, unclosed bracket, invalid key) so the playbook can run and the analyzer can check the rest of the file.

### Ask yourself whether

- The file has syntax or structural errors reported by the parser.

### Recommended practices

- Check the reported line and context. Use consistent indentation (spaces only), quote special characters, and close brackets and quotes. Run ansible-playbook --syntax-check.

## Noncompliant code example

```yaml
# File with parse error (e.g. bad indent, unclosed [)
```

## Compliant solution

```yaml
- hosts: all
  tasks:
    - name: Valid task
      debug:
        msg: ok
```

## See also

- [Ansible - YAML Syntax](https://docs.ansible.com/ansible/latest/reference_appendices/YAMLSyntax.html)
