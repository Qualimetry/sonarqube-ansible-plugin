# Avoid unversioned latest in package installs

`qa-pin-version-not-latest` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; optional

Using latest or no version for packages leads to different versions across runs. Pin a version for reproducible installs.

### Ask yourself whether

- You install packages without a version.

### Recommended practices

- Pin versions in vars or role defaults. Avoid state: latest in production.

## Noncompliant code example

```yaml
- package:
    name: nginx
    state: latest
```

## Compliant solution

```yaml
- package:
    name: "nginx={{ nginx_version }}"
    state: present
```

## See also

- [Ansible - package module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/package_module.html)
