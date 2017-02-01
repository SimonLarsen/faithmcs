package dk.sdu.compbio.netgale;

import dk.sdu.compbio.netgale.network.Node;

import java.util.List;

public class Alignment {
    private final List<List<Node>> alignment;

    public Alignment(List<List<Node>> alignment) {
        this.alignment = alignment;
    }

    public List<List<Node>> getAlignment() {
        return alignment;
    }
}
