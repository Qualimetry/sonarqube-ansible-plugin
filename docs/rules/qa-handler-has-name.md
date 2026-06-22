# Follow handler naming conventions

`qa-handler-has-name` &middot; Naming &middot; Code Smell &middot; severity MINOR &middot; enabled in the default profile

Handlers should have a name so they can be identified in run output and when using notify. Unnamed handlers show only the module name, which makes debugging and auditing harder.

### Ask yourself whether

- Someone will need to see which handler ran from logs or output.
- You use notify with handler names and want them to be descriptive.

### Recommended practices

- Add a short, descriptive name to each handler. Place name first, as with tasks.

## Noncompliant code example

```yaml
handlers:
  - service:
      name: nginx
      state: restarted
```

## Compliant solution

```yaml
handlers:
  - name: Restart nginx
    service:
      name: nginx
      state: restarted
```

## See also

- [Ansible - Handlers](https://docs.ansible.com/ansible/latest/user_guide/playbooks_handlers.html)
