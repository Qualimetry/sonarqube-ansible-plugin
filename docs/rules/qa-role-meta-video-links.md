# Remove or fix meta video links

`qa-role-meta-video-links` &middot; Roles &middot; Code Smell &middot; severity INFO &middot; optional

Role meta/main.yml may list video links (e.g. for Galaxy). Invalid or broken links should be removed or updated so documentation remains useful and lint checks pass.

### Ask yourself whether

- meta/main.yml contains video_url or similar links that are broken or outdated.

### Recommended practices

- Remove video links if not maintained, or update them to valid URLs.

## See also

- [Galaxy - Role metadata](https://galaxy.ansible.com/docs/contributing/metadata.html)
