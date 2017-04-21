package dk.sdu.compbio.faithmcs.network.io;

import dk.sdu.compbio.faithmcs.network.Edge;
import dk.sdu.compbio.faithmcs.network.Node;
import org.jgrapht.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class SIFExporter implements Exporter {
    public void write(Graph<Node,Edge> network, File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        for(Edge e : network.edgeSet()) {
            String label = e.getLabel().trim().length() > 0 ? e.getLabel() : "?";
            pw.write(String.format("%s\t%s\t%s\n", e.getSource().getLabel(), label, e.getTarget().getLabel()));
        }
        pw.close();
    }
}
