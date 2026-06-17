# Fix Galaxy metadata or dependencies

`qa-role-galaxy-deps` &middot; Roles &middot; Code Smell &middot; severity INFO &middot; optional

Roles published to Galaxy or used with ansible-galaxy must have valid meta/main.yml and dependencies. Fix any Galaxy metadata or dependency errors so the role can be installed and used correctly.

### Ask yourself whether

- You publish or consume this role via Galaxy.
- meta/main.yml has invalid dependencies or metadata.

### Recommended practices

- Ensure meta/main.yml has valid galaxy_info and dependencies. Run ansible-galaxy install or ansible-lint to validate.

## See also

- [Galaxy - Creating a role](https://galaxy.ansible.com/docs/contributing/creating_role.html)
