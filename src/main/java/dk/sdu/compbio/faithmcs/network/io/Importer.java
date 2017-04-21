package dk.sdu.compbio.faithmcs.network.io;

import dk.sdu.compbio.faithmcs.network.Edge;
import dk.sdu.compbio.faithmcs.network.Node;
import org.jgrapht.Graph;

import java.io.File;
import java.io.FileNotFoundException;

interface Importer {
    void read(Graph<Node,Edge> network, File file) throws FileNotFoundException, ImportException;
}
