# Do not store secrets in plain vars

`qa-secrets-not-in-vars` &middot; Security &middot; Code Smell &middot; severity BLOCKER &middot; optional

Storing secrets in plain variable files (e.g. `vars/main.yml`, `group_vars`, or defaults) exposes them to anyone with access to the repository or the files on disk. Those files are often committed to version control or copied to servers, so credentials can be leaked and reused.

Variables in role or playbook vars are loaded as plain YAML. There is no built-in encryption, so passwords, API keys, and tokens stored there are visible in source code, backups, and any artifact that includes those files.

Use a secure mechanism for secrets: Ansible Vault for encrypted vars, or a secrets manager (e.g. HashiCorp Vault, cloud provider secrets) that injects values at runtime. Do not store secrets in unencrypted variable files.

### Ask yourself whether

- This variable holds a password, token, API key, or other secret.
- The file or repo is (or could be) accessible to people who should not see the value.
- Secrets are committed to version control or shipped in plain form.

### Recommended secure coding practices

- Store secrets in Ansible Vault–encrypted files and reference them via variables; decrypt only at runtime.
- Use a secrets manager or CI secrets and pass values into the playbook via environment variables or extra vars that are not committed.
- Never commit unencrypted secrets to version control; use `ansible-vault encrypt` for any file that must contain secrets.

## Noncompliant code example

```yaml
# vars/main.yml
db_password: "supersecret123"
api_key: "key-abc"
```

## Compliant solution

```yaml
# Use Ansible Vault for vars that hold secrets (e.g. vars/vault.yml)
# and pass with: ansible-playbook -e @vault.yml --ask-vault-pass
# Or use environment variables / CI secrets and pass via -e "db_password={{ lookup('env', 'DB_PASS') }}"
```

## See also

- [Ansible Vault](https://docs.ansible.com/ansible/latest/vault_guide/index.html)
- [CWE-798 - Use of Hard-coded Credentials](https://cwe.mitre.org/data/definitions/798.html)
- [CWE-522 - Insufficiently Protected Credentials](https://cwe.mitre.org/data/definitions/522.html)
