package dk.sdu.compbio.faithmcs.network;

import org.jgrapht.graph.SimpleGraph;

public class UndirectedNetwork extends SimpleGraph<Node, Edge> {
    public UndirectedNetwork() {
        super(Edge.class);
    }
}
