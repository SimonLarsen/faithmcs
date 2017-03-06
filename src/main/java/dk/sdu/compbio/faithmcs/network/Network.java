package dk.sdu.compbio.faithmcs.network;

import org.jgrapht.graph.SimpleGraph;

public class Network extends SimpleGraph<Node, Edge> {
    public Network() {
        super(Edge.class);
    }
}
