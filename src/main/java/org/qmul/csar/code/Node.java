package org.qmul.csar.code;

import org.qmul.csar.lang.LanguageElement;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final LanguageElement data;
    private List<Node> nodes = new ArrayList<>();

    public Node(LanguageElement data) {
        this.data = data;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public LanguageElement getData() {
        return data;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return String.format("Node{data=%s, nodes=%s}", data, nodes);
    }
}
