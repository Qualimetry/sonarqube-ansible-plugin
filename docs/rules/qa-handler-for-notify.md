# Define handler when notified

`qa-handler-for-notify` &middot; Best Practices &middot; Bug &middot; severity MAJOR &middot; enabled in the default profile

When a task uses notify, a handler with that name must be defined (e.g. in handlers/main.yml). If the handler is missing, Ansible will report an error at the end of the play. Define the handler so notified actions run correctly.

### Ask yourself whether

- You use notify: Some name in a task.
- A handler with that exact name exists in the play or role.

### Recommended practices

- Define each notified name as a handler in the handlers section. Use descriptive handler names (e.g. Restart nginx) and match them exactly in notify.

## Noncompliant code example

```yaml
tasks:
  - name: Edit config
    template:
      src: a.conf.j2
      dest: /etc/a.conf
    notify: Restart app
# No handler named "Restart app" defined
```

## Compliant solution

```yaml
tasks:
  - name: Edit config
    template:
      src: a.conf.j2
      dest: /etc/a.conf
    notify: Restart app
handlers:
  - name: Restart app
    service:
      name: app
      state: restarted
```

## See also

- [Ansible - Handlers](https://docs.ansible.com/ansible/latest/user_guide/playbooks_handlers.html)
