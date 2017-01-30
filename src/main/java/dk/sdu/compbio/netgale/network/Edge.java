package dk.sdu.compbio.netgale.network;

import org.jgrapht.graph.DefaultEdge;

public class Edge extends DefaultEdge {
    private final Node source, target;

    public Edge(Node source, Node target) {
        this.source = source;
        this.target = target;
    }

    @Override
    public Node getSource() {
        return source;
    }

    @Override
    public Node getTarget() {
        return target;
    }
}
