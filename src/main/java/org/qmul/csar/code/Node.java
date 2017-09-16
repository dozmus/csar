package org.qmul.csar.code;

import org.qmul.csar.lang.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Node implements Statement {

    private final Statement data;
    private List<Node> nodes = new ArrayList<>();

    public Node(Statement data) {
        this.data = data;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public Statement getData() {
        return data;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(data, node.data) && Objects.equals(nodes, node.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, nodes);
    }

    @Override
    public String toString() {
        return String.format("Node{data=%s, nodes=%s}", data, nodes);
    }
}
