# Follow role directory structure

`qa-role-dir-layout` &middot; Roles &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Roles should follow the standard directory layout (tasks/, handlers/, vars/, defaults/, templates/, files/, meta/) so tools and humans can find content. Non-standard layout can break includes and Galaxy.

### Ask yourself whether

- Your role uses non-standard directory names or locations.

### Recommended practices

- Use tasks/main.yml, handlers/main.yml, vars/main.yml, defaults/main.yml, templates/, files/, meta/main.yml. Add other dirs (e.g. library/) only when needed.

## See also

- [Ansible - Roles](https://docs.ansible.com/ansible/latest/user_guide/playbooks_reuse_roles.html)
