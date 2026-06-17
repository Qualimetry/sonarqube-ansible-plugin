# Avoid unsafe read of file contents

`qa-safe-file-read` &middot; Security &middot; Code Smell &middot; severity CRITICAL &middot; enabled in the default profile

Reading file contents from the control node (e.g. with `slurp` or `lookup('file')`) and using them in tasks can expose sensitive data in logs or variables if the file contains secrets. Reading from the remote with `slurp` and then registering the result can also leak content if the result is used in a way that gets logged.

Ensure that file reads for sensitive content are not logged and that variables holding file content are not exposed. Prefer modules that operate on paths (e.g. copy, template) or use no_log and avoid passing sensitive content through task output.

### Ask yourself whether

- You read a file that may contain secrets (keys, passwords, tokens).
- The content is stored in a variable or register that might appear in logs or debug output.

### Recommended secure coding practices

- Use `no_log: true` for tasks that read or use sensitive file content. Avoid registering sensitive content and then using it in a way that is logged.
- Prefer Ansible Vault or a secrets manager for sensitive data instead of reading from plain files.

## Noncompliant code example

```yaml
- name: Read secret file
  slurp:
    src: /etc/secret
  register: secret_content
- name: Use it
  debug:
    var: secret_content.content
```

## Compliant solution

```yaml
- name: Read secret file
  slurp:
    src: /etc/secret
  register: secret_content
  no_log: true
- name: Use it (do not log)
  some_module:
    secret: "{{ secret_content.content | b64decode }}"
  no_log: true
```

## See also

- [Ansible - slurp module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/slurp_module.html)
- [CWE-532 - Insertion of Sensitive Information into Log File](https://cwe.mitre.org/data/definitions/532.html)
