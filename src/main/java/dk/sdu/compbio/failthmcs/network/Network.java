package dk.sdu.compbio.failthmcs.network;

import org.jgrapht.graph.SimpleGraph;

public class Network extends SimpleGraph<Node, dk.sdu.compbio.failthmcs.network.Edge> {
    public Network() {
        super(dk.sdu.compbio.failthmcs.network.Edge.class);
    }
}
