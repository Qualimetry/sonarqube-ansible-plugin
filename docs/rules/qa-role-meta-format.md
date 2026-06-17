# Fix meta/main.yml content

`qa-role-meta-format` &middot; Naming &middot; Code Smell &middot; severity INFO &middot; optional

Role meta/main.yml must be valid YAML and contain expected keys (galaxy_info, dependencies). Fix format or content so the role is valid for Galaxy.

### Ask yourself whether

- meta/main.yml has syntax or structural errors.

### Recommended practices

- Follow the meta/main.yml schema. Use a linter or ansible-galaxy to validate.

## See also

- [Galaxy - Role metadata](https://galaxy.ansible.com/docs/contributing/metadata.html)
