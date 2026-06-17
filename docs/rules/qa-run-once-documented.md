# Use run_once with care

`qa-run-once-documented` &middot; Best Practices &middot; Code Smell &middot; severity MINOR &middot; optional

run_once runs the task on one host only; other hosts skip it. Use only when the task must run once (e.g. coordinator). Document why run_once is needed.

### Ask yourself whether

- This task must run on exactly one host.

### Recommended practices

- Use run_once for coordination or registering a value once. Avoid for tasks that must run on every host.

## Noncompliant code example

```yaml
- package: { name: foo }
  run_once: true  # wrong: install should run on all
```

## Compliant solution

```yaml
- set_fact:
    cluster_id: "{{ ansible_date_time.epoch }}"
  run_once: true
```

## See also

- [Ansible - run_once](https://docs.ansible.com/ansible/latest/user_guide/playbooks_delegation.html#run-once)
