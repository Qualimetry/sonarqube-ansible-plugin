# Avoid risky shell piping

`qa-shell-pipe-safe` &middot; Security &middot; Code Smell &middot; severity CRITICAL &middot; enabled in the default profile

In a shell pipeline, the return code is by default that of the last command. If an earlier command fails (e.g. `cat` fails), the pipeline can still succeed and produce wrong or empty output. That can hide failures and cause incorrect behavior.

Use `set -o pipefail` so the pipeline fails when any command in it fails, or split the work into separate tasks so each step is checked.

### Ask yourself whether

- You use a pipe in a shell or command task.
- Failure of the first or middle command should fail the task.

### Recommended practices

- Prefer `shell: set -o pipefail && your | pipeline` so the task fails if any part of the pipeline fails.
- Or use the command module with argv (e.g. `wc -l /etc/hosts`) to avoid pipes where possible.

## Noncompliant code example

```yaml
- name: Get count
  shell: cat /etc/hosts | wc -l
```

## Compliant solution

```yaml
- name: Get count
  shell: set -o pipefail && cat /etc/hosts | wc -l
```

## See also

- [Bash - The Set Builtin (pipefail)](https://www.gnu.org/software/bash/manual/html_node/The-Set-Builtin.html)
