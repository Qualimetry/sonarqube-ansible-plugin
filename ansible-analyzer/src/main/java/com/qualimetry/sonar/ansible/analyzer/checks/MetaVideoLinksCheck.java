/*
 * Copyright 2026 SHAZAM Analytics Ltd
 */
package com.qualimetry.sonar.ansible.analyzer.checks;

import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleMeta;
import com.qualimetry.sonar.ansible.analyzer.visitor.BaseCheck;
import org.sonar.check.Rule;

import java.util.regex.Pattern;

/**
 * Validates video link format in role meta (galaxy_info.video_links). Flags invalid URLs.
 */
@Rule(key = "qa-role-meta-video-links")
public class MetaVideoLinksCheck extends BaseCheck {

    private static final Pattern URL_LIKE = Pattern.compile("^https?://[^\\s]+$", Pattern.CASE_INSENSITIVE);

    @Override
    public void visitPlaybookFile(PlaybookFile file) {
    }

    @Override
    public void visitRoleMeta(RoleMeta meta) {
        if (meta.parseError() != null) return;
        for (String link : meta.videoLinks()) {
            if (link == null || link.isBlank()) continue;
            if (!URL_LIKE.matcher(link.trim()).matches()) {
                addFileIssue("Invalid video link format in galaxy_info.video_links: " + link);
            }
        }
    }
}
