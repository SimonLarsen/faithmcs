package dk.sdu.compbio.netgale.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class SIFExporter {
    public static void write(Network network, File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        for(Edge e : network.edgeSet()) {
            pw.write(String.format("%s\t-\t%s\n", e.getSource().getLabel(), e.getTarget().getLabel()));
        }
        pw.close();
    }
}
