# Pin package versions instead of latest

`qa-pin-package-version` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; optional

Package tasks that use "latest" or no version are not reproducible. Pin versions (via role defaults, vars, or package name) so installs are consistent across environments and time.

### Ask yourself whether

- You use state: latest or omit version.

### Recommended practices

- Set a specific version in vars or role defaults and use it in the package task. Avoid state: latest in production.

## Noncompliant code example

```yaml
- apt:
    name: nginx
    state: latest
```

## Compliant solution

```yaml
- apt:
    name: "nginx={{ nginx_version }}"
    state: present
```

## See also

- [Ansible - apt module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/apt_module.html)
