# Use HTTPS instead of HTTP

`qa-require-https` &middot; Security &middot; Code Smell &middot; severity CRITICAL &middot; optional

Using plain HTTP for downloads or API calls allows network eavesdropping and man-in-the-middle attacks. Credentials and data can be read or modified in transit.

Modules such as `get_url`, `uri`, and `community.general.rhsm` send requests to URLs you provide. When the URL uses `http://`, traffic is unencrypted. Attackers on the same network (or in a position to intercept traffic) can capture or alter the content and any credentials sent in headers or the URL.

Use HTTPS (or another secure channel) for any URL that touches sensitive data or authentication. Reserve HTTP only for truly local or trusted endpoints where risk has been accepted.

### Ask yourself whether

- The URL points to an external or untrusted host.
- The request sends or receives credentials or other sensitive data.
- Playbooks run in an environment where network traffic could be intercepted.

### Recommended secure coding practices

- Use `https://` for all external or sensitive URLs.
- Validate TLS when possible (e.g. use valid certificates; avoid disabling verification unless necessary and documented).
- Prefer package or role-based distribution for internal artifacts instead of HTTP downloads where feasible.

## Noncompliant code example

```yaml
- name: Download file
  get_url:
    url: http://example.com/file.tar.gz
    dest: /tmp/file.tar.gz
```

## Compliant solution

```yaml
- name: Download file
  get_url:
    url: https://example.com/file.tar.gz
    dest: /tmp/file.tar.gz
```

## See also

- [Ansible - get_url module](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/get_url_module.html)
- [CWE-319 - Cleartext Transmission of Sensitive Information](https://cwe.mitre.org/data/definitions/319.html)
