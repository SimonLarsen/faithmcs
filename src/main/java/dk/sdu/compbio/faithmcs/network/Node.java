package dk.sdu.compbio.faithmcs.network;

public class Node {
    private final String label;
    private final int hash_code;
    private final boolean fake;

    private int position;

    public Node(String label, boolean fake) {
        this.label = label;
        this.hash_code = label.hashCode();
        this.fake = fake;
    }

    public Node(String label) {
        this(label, false);
    }

    public String getLabel() {
        return label;
    }

    public boolean isFake() { return fake; }

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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
