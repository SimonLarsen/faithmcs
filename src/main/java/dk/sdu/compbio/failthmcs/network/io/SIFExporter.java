package dk.sdu.compbio.failthmcs.network.io;

import dk.sdu.compbio.failthmcs.network.Edge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class SIFExporter implements Exporter {
    public void write(dk.sdu.compbio.failthmcs.network.Network network, File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        for(Edge e : network.edgeSet()) {
            pw.write(String.format("%s\t-\t%s\n", e.getSource().getLabel(), e.getTarget().getLabel()));
        }
        pw.close();
    }
}
