package dk.sdu.compbio.netgale.network;

import org.jgrapht.graph.DefaultEdge;

public class Edge extends DefaultEdge {
    private final Node source, target;
    private final int hash_code;

    public Edge(Node source, Node target) {
        this.source = source;
        this.target = target;

        this.hash_code = source.hashCode()+target.hashCode();
    }

    @Override
    public Node getSource() {
        return source;
    }

    @Override
    public Node getTarget() {
        return target;
    }

    @Override
    public int hashCode() {
        return hash_code;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null) return false;
        if(!(o instanceof Edge)) return false;

        Edge e = (Edge)o;
        return source == e.getSource() && target == e.getTarget();
        /*
        return (source == e.getSource() && target == e.getTarget()) ||
               (source == e.getTarget() && target == e.getSource());
        */
    }
}
