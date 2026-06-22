# Fix sanity check failure

`qa-runtime-sanity` &middot; Reliability &middot; Code Smell &middot; severity INFO &middot; optional

This rule is a placeholder for ansible-test sanity results. The analyzer does not run ansible-test sanity (that would require Ansible runtime). Run ansible-test sanity locally and fix any reported issues. When active, this rule may report one informational message per file reminding you to run sanity locally.

### Ask yourself whether

- You run ansible-test sanity in CI or before release.

### Recommended practices

- Run ansible-test sanity in your pipeline and fix failures. Use this rule as a reminder to run it; actual checks are done by ansible-test.

## See also

- [Ansible - Sanity tests](https://docs.ansible.com/ansible/latest/dev_guide/testing_sanity.html)
