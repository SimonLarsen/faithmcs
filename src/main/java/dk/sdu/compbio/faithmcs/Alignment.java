package dk.sdu.compbio.faithmcs;

import dk.sdu.compbio.faithmcs.network.Node;
import org.jgrapht.Graph;

import java.util.List;

public interface Alignment<T,U> {
    List<List<Node>> getAlignment();
    Graph<T,U> buildNetwork(int exceptions, boolean remove_leaf_exceptions);
}
