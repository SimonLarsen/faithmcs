package dk.sdu.compbio.netgale.network;

public class Node {
    private final String label;
    private final int hash_code;

    public Node(String label) {
        this.label = label;
        this.hash_code = label.hashCode();
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public int hashCode() {
        return hash_code;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null) return false;
        if(!(o instanceof Node)) return false;

        Node n = (Node)o;
        return label.equals(n.getLabel());
    }
}
