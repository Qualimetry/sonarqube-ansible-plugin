# Avoid relative paths in critical arguments

`qa-absolute-or-role-paths` &middot; Security &middot; Code Smell &middot; severity MAJOR &middot; enabled in the default profile

Relative paths in module arguments (e.g. `src`, `file`) are resolved from the current working directory, which can differ depending on how the playbook is run. That leads to "file not found" or using the wrong file when run from another directory or from CI.

Use absolute paths or role-relative paths (e.g. `{{ role_path }}/files/...`) so behavior is consistent regardless of where the playbook is executed from.

### Ask yourself whether

- You use relative paths like `files/config.yml` or `../templates/app.conf` in src, file, or similar arguments.
- Playbooks are run from different working directories (e.g. project root vs role directory).

### Recommended practices

- For role content, use `{{ role_path }}/files/...` or `{{ role_path }}/templates/...`. For playbook-relative paths, use a variable or absolute path.
- Avoid paths that depend on the current working directory.

## Noncompliant code example

```yaml
- name: Copy from relative path
  copy:
    src: files/app.conf
    dest: /etc/app.conf
```

## Compliant solution

```yaml
- name: Copy from role path
  copy:
    src: "{{ role_path }}/files/app.conf"
    dest: /etc/app.conf
```

## See also

- [Ansible - Search paths in Ansible](https://docs.ansible.com/ansible/latest/playbook_guide/playbook_pathing.html)
