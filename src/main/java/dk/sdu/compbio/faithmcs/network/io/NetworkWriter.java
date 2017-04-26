package dk.sdu.compbio.faithmcs.network.io;

import dk.sdu.compbio.faithmcs.network.Edge;
import dk.sdu.compbio.faithmcs.network.Node;
import org.jgrapht.Graph;

import java.io.File;
import java.io.FileNotFoundException;

public class NetworkWriter {
    public static void write(Graph<Node,Edge> network, File file) throws FileNotFoundException {
        String path = file.getPath();
        int dotpos = path.lastIndexOf('.');
        if(dotpos == -1 || dotpos == path.length()) {
            throw new IllegalArgumentException("Cannot determine file type from filename");
        }
        String ending = path.substring(dotpos+1);

        Exporter exporter = null;

        switch(ending) {
            case "sif":
                exporter = new SIFExporter(); break;
        }

        if(exporter == null) throw new IllegalArgumentException("Unrecognized file format: " + ending);

        exporter.write(network, file);
    }
}
