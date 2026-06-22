# Fix meta runtime configuration

`qa-role-meta-runtime` &middot; Roles &middot; Code Smell &middot; severity INFO &middot; optional

Role meta/main.yml can specify runtime requirements (e.g. Ansible version, platform). Fix any invalid or inconsistent runtime configuration so the role runs correctly in target environments.

### Ask yourself whether

- meta/main.yml has min_ansible_version or platform entries that are wrong or incomplete.

### Recommended practices

- Set min_ansible_version and platform list in galaxy_info when the role has requirements. Keep them accurate.

## See also

- [Galaxy - Role metadata](https://galaxy.ansible.com/docs/contributing/metadata.html)
