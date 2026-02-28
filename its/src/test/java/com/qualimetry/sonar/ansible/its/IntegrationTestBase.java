/*
 * Copyright 2026 SHAZAM Analytics Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qualimetry.sonar.ansible.its;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonSyntaxException;

/**
 * Base class for Ansible plugin integration tests against a running SonarQube server (UAT).
 *
 * <p>Provides: server availability check, run sonar-scanner, wait for analysis,
 * provision project, and API GET/POST. Projects are left on the server after tests
 * for study (no delete).
 */
abstract class IntegrationTestBase {

    protected static final String SONAR_URL =
            System.getProperty("sonar.host.url", "http://localhost:9000");
    protected static final String SONAR_TOKEN =
            System.getProperty("sonar.token", "");
    protected static final String REPOSITORY_KEY = "qualimetry-ansible";
    protected static final String LANGUAGE_KEY = "ansible";

    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);
    private static final int CE_POLL_MAX_ATTEMPTS = 60;
    private static final long CE_POLL_INTERVAL_MS = 2000;
    private static final long CE_FALLBACK_WAIT_MS = 15_000;
    private static final long SCANNER_TIMEOUT_MINUTES = 5;

    protected static void assumeServerAvailable() {
        try {
            HttpRequest request = newGetRequest("/api/system/status");
            HttpResponse<String> response = HTTP_CLIENT.send(request,
                    HttpResponse.BodyHandlers.ofString());
            org.junit.jupiter.api.Assumptions.assumeTrue(response.statusCode() == 200,
                    "SonarQube server not available at " + SONAR_URL + " (status=" + response.statusCode() + ")");
            String status = "?";
            try {
                JsonElement el = GSON.fromJson(response.body(), JsonElement.class);
                if (el != null && el.isJsonObject() && el.getAsJsonObject().has("status")) {
                    status = el.getAsJsonObject().get("status").getAsString();
                }
            } catch (Exception e) {
                // Some proxies or versions return non-JSON (e.g. HTML). Treat 200 as reachable.
            }
            org.junit.jupiter.api.Assumptions.assumeTrue("UP".equals(status) || "?".equals(status),
                    "SonarQube server is not in UP status (got: " + status + ")");
        } catch (Exception e) {
            org.junit.jupiter.api.Assumptions.assumeTrue(false,
                    "Cannot connect to SonarQube server at " + SONAR_URL + ": " + e.getMessage());
        }
    }

    /**
     * Runs analysis against the project directory. If {@code sonar.scanner.bin} is set, uses that
     * executable (e.g. C:\sqa\node\scanners\sonar\bin\sonar-scanner.bat). Otherwise uses Maven
     * sonar:sonar when the project has a pom.xml, or sonar-scanner from PATH. Uses absolute
     * projectBaseDir for source and Code tab.
     */
    protected static void runScan(Path projectDir, String projectKey)
            throws IOException, InterruptedException {
        String absoluteBase = projectDir.toAbsolutePath().toString();
        ProcessBuilder pb;
        String scanTool;
        String scannerBin = System.getProperty("sonar.scanner.bin", "").trim();
        if (!scannerBin.isEmpty()) {
            scanTool = "sonar-scanner (" + scannerBin + ")";
            pb = new ProcessBuilder(
                    scannerBin,
                    "-Dsonar.projectKey=" + projectKey,
                    "-Dsonar.host.url=" + SONAR_URL,
                    "-Dsonar.token=" + SONAR_TOKEN,
                    "-Dsonar.projectBaseDir=" + absoluteBase,
                    "-Dsonar.sources=.",
                    "-Dsonar.inclusions=**/*.yml,**/*.yaml",
                    "-Dsonar.ansible.file.suffixes=yml,yaml",
                    "-Dsonar.lang.patterns.ansible=**/*.yml,**/*.yaml",
                    "-Dsonar.lang.patterns.yaml=**/.nomatch-yaml",
                    "-Dsonar.language=ansible"
            );
        } else if (Files.exists(projectDir.resolve("pom.xml"))) {
            scanTool = "Maven sonar:sonar";
            List<String> cmd = new ArrayList<>();
            cmd.add(getMavenCommand());
            cmd.add("-q");
            cmd.add("sonar:sonar");
            cmd.add("-Dsonar.host.url=" + SONAR_URL);
            cmd.add("-Dsonar.token=" + SONAR_TOKEN);
            cmd.add("-Dsonar.projectKey=" + projectKey);
            cmd.add("-Dsonar.projectBaseDir=" + absoluteBase);
            cmd.add("-Dsonar.sources=.");
            cmd.add("-Dsonar.inclusions=**/*.yml,**/*.yaml");
            cmd.add("-Dsonar.ansible.file.suffixes=yml,yaml");
            cmd.add("-Dsonar.lang.patterns.ansible=**/*.yml,**/*.yaml");
            cmd.add("-Dsonar.lang.patterns.yaml=**/.nomatch-yaml");
            cmd.add("-Dsonar.language=ansible");
            pb = new ProcessBuilder(cmd);
        } else {
            scanTool = "sonar-scanner";
            pb = new ProcessBuilder(
                    getScannerCommand(),
                    "-Dsonar.projectKey=" + projectKey,
                    "-Dsonar.host.url=" + SONAR_URL,
                    "-Dsonar.token=" + SONAR_TOKEN,
                    "-Dsonar.projectBaseDir=" + absoluteBase,
                    "-Dsonar.sources=.",
                    "-Dsonar.inclusions=**/*.yml,**/*.yaml",
                    "-Dsonar.ansible.file.suffixes=yml,yaml",
                    "-Dsonar.lang.patterns.ansible=**/*.yml,**/*.yaml",
                    "-Dsonar.lang.patterns.yaml=**/.nomatch-yaml",
                    "-Dsonar.language=ansible"
            );
        }
        pb.directory(projectDir.toFile());
        pb.inheritIO();
        Process process = pb.start();
        boolean finished = process.waitFor(SCANNER_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException(
                    scanTool + " timed out after " + SCANNER_TIMEOUT_MINUTES + " minutes");
        }
        if (process.exitValue() != 0) {
            throw new RuntimeException(
                    scanTool + " exited with code " + process.exitValue());
        }
    }

    protected static void waitForAnalysisToComplete(String projectKey) throws Exception {
        String encodedKey = URLEncoder.encode(projectKey, StandardCharsets.UTF_8);
        String path = "/api/ce/activity?component=" + encodedKey + "&status=PENDING,IN_PROGRESS";
        for (int attempt = 0; attempt < CE_POLL_MAX_ATTEMPTS; attempt++) {
            try {
                JsonObject response = apiGet(path);
                JsonArray tasks = response.getAsJsonArray("tasks");
                if (tasks == null || tasks.isEmpty()) {
                    return;
                }
            } catch (RuntimeException e) {
                String msg = e.getMessage() != null ? e.getMessage() : "";
                if (msg.contains("403") || msg.contains("non-JSON") || msg.contains("not an object")
                        || msg.contains("JsonPrimitive") || msg.contains("JsonSyntax")) {
                    Thread.sleep(CE_FALLBACK_WAIT_MS);
                    return;
                }
                throw e;
            }
            Thread.sleep(CE_POLL_INTERVAL_MS);
        }
        throw new RuntimeException(
                "Analysis did not complete within "
                        + (CE_POLL_MAX_ATTEMPTS * CE_POLL_INTERVAL_MS / 1000)
                        + " seconds for project: " + projectKey);
    }

    protected static void provisionProject(String projectKey, String name) throws Exception {
        String body = "project=" + URLEncoder.encode(projectKey, StandardCharsets.UTF_8)
                + "&name=" + URLEncoder.encode(name, StandardCharsets.UTF_8);
        HttpRequest request = newPostRequest("/api/projects/create", body);
        HttpResponse<String> response = HTTP_CLIENT.send(request,
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 400) {
            throw new RuntimeException(
                    "Failed to provision project '" + projectKey + "': "
                            + response.statusCode() + " " + response.body());
        }
    }

    /**
     * Deletes a project from the server. Ignores errors (e.g. project does not exist).
     * Use before provisioning for a clean project.
     */
    protected static void deleteProject(String projectKey) {
        try {
            String body = "project=" + URLEncoder.encode(projectKey, StandardCharsets.UTF_8);
            HttpRequest request = newPostRequest("/api/projects/delete", body);
            HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            // Ignore - project may not exist or token may lack permission
        }
    }

    protected static JsonObject apiGet(String path) throws Exception {
        HttpRequest request = newGetRequest(path);
        HttpResponse<String> response = HTTP_CLIENT.send(request,
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "API GET " + path + " returned " + response.statusCode()
                            + ": " + response.body());
        }
        String body = response.body();
        if (body != null && body.trim().toLowerCase().startsWith("<")) {
            throw new RuntimeException("API GET " + path + " returned non-JSON (e.g. HTML), status=200.");
        }
        try {
            JsonElement el = GSON.fromJson(body, JsonElement.class);
            if (el == null || !el.isJsonObject()) {
                throw new RuntimeException("API GET " + path + " returned JSON but not an object.");
            }
            return el.getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("API GET " + path + " returned non-JSON (parse error). " + e.getMessage());
        }
    }

    protected static JsonObject apiPost(String path, String formBody) throws Exception {
        HttpRequest request = newPostRequest(path, formBody);
        HttpResponse<String> response = HTTP_CLIENT.send(request,
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "API POST " + path + " returned " + response.statusCode()
                            + ": " + response.body());
        }
        return GSON.fromJson(response.body(), JsonObject.class);
    }

    private static HttpRequest newGetRequest(String path) {
        String uri = SONAR_URL.endsWith("/") && path.startsWith("/") ? SONAR_URL + path.substring(1) : SONAR_URL + path;
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(REQUEST_TIMEOUT)
                .header("Accept", "application/json")
                .GET();
        addAuth(builder);
        return builder.build();
    }

    private static HttpRequest newPostRequest(String path, String formBody) {
        String uri = SONAR_URL.endsWith("/") && path.startsWith("/") ? SONAR_URL + path.substring(1) : SONAR_URL + path;
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(formBody));
        addAuth(builder);
        return builder.build();
    }

    private static void addAuth(HttpRequest.Builder builder) {
        if (SONAR_TOKEN != null && !SONAR_TOKEN.isBlank()) {
            // SonarQube 9.x+: user tokens often use Bearer. Older or some setups use Basic (token:empty).
            if (SONAR_TOKEN.startsWith("sqa_")) {
                builder.header("Authorization", "Bearer " + SONAR_TOKEN);
            } else {
                String credentials = SONAR_TOKEN + ":";
                String encoded = Base64.getEncoder()
                        .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
                builder.header("Authorization", "Basic " + encoded);
            }
        }
    }

    private static String getScannerCommand() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("win") ? "sonar-scanner.bat" : "sonar-scanner";
    }

    private static String getMavenCommand() {
        String os = System.getProperty("os.name", "").toLowerCase();
        return os.contains("win") ? "mvn.cmd" : "mvn";
    }
}
