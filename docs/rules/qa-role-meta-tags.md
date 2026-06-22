# Add tags to meta/main.yml

`qa-role-meta-tags` &middot; Roles &middot; Code Smell &middot; severity INFO &middot; optional

Adding a tags list to meta/main.yml allows the role to be selected with --tags or --skip-tags when included in a playbook. Omission makes it harder to run subsets of roles.

### Ask yourself whether

- You want to filter this role with --tags.

### Recommended practices

- Add galaxy_info.galaxy_tags (or equivalent) with meaningful tag names in meta/main.yml.

## See also

- [Galaxy - Role metadata](https://galaxy.ansible.com/docs/contributing/metadata.html)
