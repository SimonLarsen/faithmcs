package dk.sdu.compbio.faithmcs.network;

import org.jgrapht.graph.SimpleDirectedGraph;

public class DirectedNetwork extends SimpleDirectedGraph<Node,Edge> {
    public DirectedNetwork() { super(Edge.class); }
}
