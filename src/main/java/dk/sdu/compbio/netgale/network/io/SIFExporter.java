package dk.sdu.compbio.netgale.network.io;

import dk.sdu.compbio.netgale.network.Edge;
import dk.sdu.compbio.netgale.network.Network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class SIFExporter implements Exporter {
    public void write(Network network, File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        for(Edge e : network.edgeSet()) {
            pw.write(String.format("%s\t-\t%s\n", e.getSource().getLabel(), e.getTarget().getLabel()));
        }
        pw.close();
    }
}
