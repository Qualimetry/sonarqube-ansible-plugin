# Use vault for secrets

`qa-secrets-in-vault` &middot; Security &middot; Code Smell &middot; severity CRITICAL &middot; optional

Secrets (passwords, keys, tokens) should be stored in Ansible Vault-encrypted files so they are not exposed in plain text in the repo or on disk. Use vault for any variable or file that holds sensitive data.

### Ask yourself whether

- You store secrets in plain vars or files.

### Recommended practices

- Encrypt files with ansible-vault encrypt and reference them with --ask-vault-pass or vault password file. Use vault for group_vars or role vars that contain secrets.

## Noncompliant code example

```yaml
# vars/main.yml with plain secret
db_password: secret123
```

## Compliant solution

```yaml
# Encrypt with ansible-vault encrypt vars/vault.yml and pass with -e @vault.yml
```

## See also

- [Ansible Vault](https://docs.ansible.com/ansible/latest/vault_guide/index.html)
