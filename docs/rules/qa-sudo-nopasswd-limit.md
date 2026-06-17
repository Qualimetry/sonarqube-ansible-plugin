# Restrict sudo NOPASSWD usage

`qa-sudo-nopasswd-limit` &middot; Security &middot; Code Smell &middot; severity CRITICAL &middot; optional

sudo NOPASSWD allows running commands as root without a password, which increases privilege escalation risk. Restrict NOPASSWD to specific commands and users where possible; avoid broad NOPASSWD for the run user.

### Ask yourself whether

- Your playbooks or roles configure sudo with NOPASSWD.
- The scope is broader than necessary (e.g. all commands vs one script).

### Recommended secure coding practices

- Limit NOPASSWD to the minimum commands required. Prefer password-based sudo when feasible. Document any NOPASSWD usage.

## See also

- [CWE-250 - Execution with Unnecessary Privileges](https://cwe.mitre.org/data/definitions/250.html)
