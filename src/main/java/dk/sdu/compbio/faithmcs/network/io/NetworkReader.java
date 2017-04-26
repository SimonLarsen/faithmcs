package dk.sdu.compbio.faithmcs.network.io;

import dk.sdu.compbio.faithmcs.network.Edge;
import dk.sdu.compbio.faithmcs.network.Node;
import org.jgrapht.Graph;

import java.io.File;
import java.io.FileNotFoundException;

public class NetworkReader {
    public static void read(Graph<Node,Edge> network, File file) throws FileNotFoundException, ImportException {
        String path = file.getPath();
        int dotpos = path.lastIndexOf('.');
        if(dotpos == -1 || dotpos == path.length()) {
            throw new IllegalArgumentException("Cannot determine file type from filename.");
        }
        String ending = path.substring(dotpos+1);

        Importer importer = null;
        switch(ending) {
            case "sif":
                importer = new SIFImporter(); break;
        }

        if(importer == null) throw new IllegalArgumentException("Unrecognized file format: " + ending);

        importer.read(network, file);
    }
}
