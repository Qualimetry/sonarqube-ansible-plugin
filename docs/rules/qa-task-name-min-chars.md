# Use sufficiently long task names

`qa-task-name-min-chars` &middot; Naming &middot; Code Smell &middot; severity INFO &middot; enabled in the default profile

Task names that are too short (e.g. one or two words with no context) provide little value in run output and logs. A minimum length encourages descriptive names that help with debugging and auditing.

Meaningful task names make it easier to find failures and understand what each task does without opening the file. Very short names like "Run" or "Copy" add little beyond the module name.

### Ask yourself whether

- The task name could be more descriptive (what is being done and for whom or what).
- Someone reading the log would understand the task from the name alone.

### Recommended practices

- Use task names that describe the action and optionally the target (e.g. "Install nginx package" instead of "Install").
- Set a minimum length in your style guide or linter and enforce it.

## Noncompliant code example

```yaml
- name: Hi
  ping:
```

## Compliant solution

```yaml
- name: Ping the host to verify connectivity
  ping:
```

## See also

- [Ansible - Intro to playbooks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_intro.html)
