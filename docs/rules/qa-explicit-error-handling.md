# Avoid ignore_errors for control flow

`qa-explicit-error-handling` &middot; Best Practices &middot; Bug &middot; severity MAJOR &middot; enabled in the default profile

Using `ignore_errors: yes` (or `true`) hides every failure for that task. The play continues as if the task succeeded, so real errors go unnoticed and can cause inconsistent state or broken automation later.

`ignore_errors` tells Ansible to treat a failed task as successful for the purpose of continuing the play. It does not distinguish between expected and unexpected failures. That makes debugging harder and can mask security or correctness issues (e.g. a failed package install or permission change that you intended to handle explicitly).

Prefer explicit error handling: use `failed_when` with a clear condition, or `rescue` in a block so that only intended cases are ignored and the rest are reported.

### Ask yourself whether

- You are using `ignore_errors` to skip a specific known condition (e.g. "command not found").
- Failure of this task could leave the system in an inconsistent or insecure state.
- Other people run or debug this playbook and need to see real failures.

### Recommended secure coding practices

- Use `failed_when: <condition>` so the task fails only when the condition is true, and succeeds otherwise.
- Use `block` and `rescue` to handle expected failures and optionally run cleanup or fallback tasks.
- If you must use `ignore_errors`, add a comment explaining why and consider registering the result and checking it in a later task.

## Noncompliant code example

```yaml
- name: Maybe fail
  command: /bin/false
  ignore_errors: yes
```

## Compliant solution

```yaml
- name: Run command, fail only when exit code is not 0 or 1
  command: /some/script
  register: result
  failed_when: result.rc not in [0, 1]

# Or use block/rescue for explicit handling:
- name: Try install, rescue on failure
  block:
    - name: Install package
      ansible.builtin.package:
        name: mypkg
        state: present
  rescue:
    - name: Log and re-raise
      ansible.builtin.debug:
        msg: "Install failed: {{ ansible_failed_result.msg }}"
      failed_when: true
```

## See also

- [Ansible - Error handling in playbooks](https://docs.ansible.com/ansible/latest/user_guide/playbooks_error_handling.html)
- [Ansible - Error handling (failed_when)](https://docs.ansible.com/ansible/latest/user_guide/playbooks_error_handling.html#defining-failure)
