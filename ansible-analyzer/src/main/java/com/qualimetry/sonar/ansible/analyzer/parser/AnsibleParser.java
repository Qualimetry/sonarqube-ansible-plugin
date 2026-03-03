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
package com.qualimetry.sonar.ansible.analyzer.parser;

import com.qualimetry.sonar.ansible.analyzer.parser.model.ParseError;
import com.qualimetry.sonar.ansible.analyzer.parser.model.Play;
import com.qualimetry.sonar.ansible.analyzer.parser.model.PlaybookFile;
import com.qualimetry.sonar.ansible.analyzer.parser.model.RoleRef;
import com.qualimetry.sonar.ansible.analyzer.parser.model.Task;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses Ansible playbook YAML into the internal tree model using SnakeYAML.
 * Detects playbooks (list of maps with hosts/tasks/roles/vars), builds PlaybookFile
 * with Play/Task list and line/column positions. On parse failure, returns a
 * PlaybookFile with a ParseError so checks can report one issue instead of crashing.
 */
public class AnsibleParser {

    /**
     * Parses playbook content. Uses SnakeYAML to parse, detects plays and tasks,
     * attaches line (and column where available). On YAML parse failure returns
     * a playbook with parseError set and empty plays.
     *
     * @param uri     file URI or path
     * @param content raw file content
     * @return parsed playbook, or playbook with parseError if parse failed
     */
    public PlaybookFile parse(String uri, String content) {
        if (content == null || content.isBlank()) {
            return new PlaybookFile(Collections.emptyList(), uri);
        }

        try {
            Node root = new Yaml().compose(new StringReader(content));
            if (root == null) {
                return new PlaybookFile(Collections.emptyList(), uri);
            }
            return buildPlaybook(uri, root);
        } catch (YAMLException e) {
            int line = 0;
            if (e instanceof MarkedYAMLException marked && marked.getProblemMark() != null) {
                line = marked.getProblemMark().getLine() + 1; // SnakeYAML Mark is 0-based line
            }
            String message = e.getMessage() != null ? e.getMessage() : "YAML parse error";
            return new PlaybookFile(Collections.emptyList(), uri, new ParseError(message, line));
        }
    }

    private static int lineOf(Node node) {
        if (node == null) return 0;
        Mark mark = node.getStartMark();
        return mark != null ? mark.getLine() + 1 : 0;
    }

    private static PlaybookFile buildPlaybook(String uri, Node root) {
        if (!(root instanceof SequenceNode seq)) {
            return new PlaybookFile(Collections.emptyList(), uri);
        }
        List<Play> plays = new ArrayList<>();
        for (Node item : seq.getValue()) {
            if (item instanceof MappingNode mapNode) {
                Play play = buildPlay(mapNode);
                if (play != null) {
                    plays.add(play);
                }
            }
        }
        return new PlaybookFile(plays, uri);
    }

    /**
     * Heuristic: a map is a play if it has at least one of hosts, tasks, roles, or vars.
     */
    private static boolean looksLikePlay(MappingNode mapNode) {
        for (NodeTuple tuple : mapNode.getValue()) {
            String key = scalarKey(tuple.getKeyNode());
            if (key != null && (key.equals("hosts") || key.equals("tasks") || key.equals("roles") || key.equals("vars"))) {
                return true;
            }
        }
        return false;
    }

    private static Play buildPlay(MappingNode mapNode) {
        if (!looksLikePlay(mapNode)) {
            return null;
        }
        int playLine = lineOf(mapNode);
        String name = null;
        List<Task> tasks = new ArrayList<>();
        List<RoleRef> roles = new ArrayList<>();
        List<String> tags = Collections.emptyList();

        for (NodeTuple tuple : mapNode.getValue()) {
            String key = scalarKey(tuple.getKeyNode());
            if (key == null) continue;
            Node valueNode = tuple.getValueNode();
            switch (key) {
                case "name" -> name = scalarValue(valueNode);
                case "tasks", "pre_tasks", "post_tasks" -> collectTasks(valueNode, tasks);
                case "roles" -> collectRoles(valueNode, roles);
                case "tags" -> tags = tagsFromValue(valueNode);
                default -> { }
            }
        }
        return new Play(name, tasks, roles, playLine, tags);
    }

    private static List<String> tagsFromValue(Node node) {
        if (node == null) return Collections.emptyList();
        if (node instanceof ScalarNode s) {
            String v = s.getValue();
            return v == null || v.isBlank() ? Collections.emptyList() : List.of(v);
        }
        if (node instanceof SequenceNode seq) {
            List<String> out = new ArrayList<>();
            for (Node item : seq.getValue()) {
                String v = scalarValue(item);
                if (v != null && !v.isBlank()) out.add(v);
            }
            return out;
        }
        return Collections.emptyList();
    }

    private static void collectTasks(Node node, List<Task> out) {
        if (node instanceof SequenceNode seq) {
            for (Node item : seq.getValue()) {
                if (item instanceof MappingNode mapNode) {
                    Node blockList = valueForKey(mapNode, "block");
                    if (blockList != null) {
                        collectTasks(blockList, out);
                    } else {
                        Task task = buildTask(mapNode);
                        if (task != null) {
                            out.add(task);
                        }
                    }
                }
            }
        }
    }

    private static Node valueForKey(MappingNode mapNode, String key) {
        for (NodeTuple tuple : mapNode.getValue()) {
            if (key.equals(scalarKey(tuple.getKeyNode()))) {
                return tuple.getValueNode();
            }
        }
        return null;
    }

    private static Task buildTask(MappingNode mapNode) {
        int line = lineOf(mapNode);
        Map<String, Object> attributes = nodeToMap(mapNode);
        if (attributes == null) attributes = new LinkedHashMap<>();
        String name = (String) attributes.get("name");
        String moduleKey = inferModuleKey(mapNode, attributes);
        return new Task(name, moduleKey, line, attributes);
    }

    /**
     * Infers the module key (FQCN or short name) from the task map.
     * Ansible tasks are either one key (module: args) or have "name" + module key.
     */
    private static String inferModuleKey(MappingNode mapNode, Map<String, Object> attributes) {
        for (NodeTuple tuple : mapNode.getValue()) {
            String key = scalarKey(tuple.getKeyNode());
            if (key == null) continue;
            if (key.equals("name") || key.equals("block") || key.equals("include_role") || key.equals("include_tasks")
                    || key.equals("import_role") || key.equals("import_tasks")) {
                continue;
            }
            if (key.equals("include") || key.equals("import_playbook")) {
                Object val = attributes.get(key);
                return key + (val != null ? ":" + val : "");
            }
            return key;
        }
        return null;
    }

    private static void collectRoles(Node node, List<RoleRef> out) {
        if (!(node instanceof SequenceNode seq)) return;
        for (Node item : seq.getValue()) {
            if (item instanceof ScalarNode scalar) {
                out.add(new RoleRef(scalar.getValue(), lineOf(item)));
            } else if (item instanceof MappingNode mapNode) {
                String role = scalarValueFromMap(mapNode, "role");
                if (role != null) {
                    out.add(new RoleRef(role, lineOf(item)));
                }
            }
        }
    }

    private static String scalarKey(Node node) {
        return node instanceof ScalarNode s ? s.getValue() : null;
    }

    private static String scalarValue(Node node) {
        return node instanceof ScalarNode s ? s.getValue() : null;
    }

    private static String scalarValueFromMap(MappingNode mapNode, String key) {
        for (NodeTuple tuple : mapNode.getValue()) {
            if (key.equals(scalarKey(tuple.getKeyNode()))) {
                return scalarValue(tuple.getValueNode());
            }
        }
        return null;
    }

    /**
     * Converts a MappingNode to a Map for use as task attributes (no position info).
     */
    private static Map<String, Object> nodeToMap(MappingNode node) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (NodeTuple tuple : node.getValue()) {
            String k = scalarKey(tuple.getKeyNode());
            if (k != null) {
                Object v = nodeToObject(tuple.getValueNode());
                if (v != null) {
                    map.put(k, v);
                }
            }
        }
        return map;
    }

    private static Object nodeToObject(Node node) {
        if (node == null) return null;
        return switch (node.getNodeId()) {
            case scalar -> ((ScalarNode) node).getValue();
            case sequence -> {
                List<Object> list = new ArrayList<>();
                for (Node n : ((SequenceNode) node).getValue()) {
                    list.add(nodeToObject(n));
                }
                yield list;
            }
            case mapping -> {
                Map<String, Object> map = new LinkedHashMap<>();
                for (NodeTuple tuple : ((MappingNode) node).getValue()) {
                    String k = scalarKey(tuple.getKeyNode());
                    if (k != null) {
                        map.put(k, nodeToObject(tuple.getValueNode()));
                    }
                }
                yield map;
            }
            default -> null;
        };
    }
}
