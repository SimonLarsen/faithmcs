package dk.sdu.compbio.faithmcs.network;

import org.jgrapht.graph.DefaultEdge;

public class Edge extends DefaultEdge {
    private final Node source, target;
    private final int conservation;
    private final String label;

    public Edge(Node source, Node target, String label, int conservation) {
        super();
        this.source = source;
        this.target = target;
        this.label = label;
        this.conservation = conservation;
    }

    public Edge(Node source, Node target, String label) {
        this(source, target, label, 0);
    }

    public Edge(Node source, Node target) {
        this(source, target, "", 0);
    }

    @Override
    public Node getSource() {
        return source;
    }

    @Override
    public Node getTarget() {
        return target;
    }

    public int getConservation() { return conservation; }

    public String getLabel() { return label; }
}
