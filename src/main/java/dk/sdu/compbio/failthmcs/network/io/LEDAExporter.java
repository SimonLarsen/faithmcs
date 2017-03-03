package dk.sdu.compbio.failthmcs.network.io;

import dk.sdu.compbio.failthmcs.network.Edge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LEDAExporter implements Exporter {
    @Override
    public void write(dk.sdu.compbio.failthmcs.network.Network network, File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);

        pw.println("LEDA.GRAPH");
        pw.println("string");
        pw.println("void");
        pw.println("-2");

        // map vertices to 1-indexed integers
        pw.println(Integer.toString(network.vertexSet().size()));
        Map<dk.sdu.compbio.failthmcs.network.Node,Integer> nodeMap = new HashMap<>();
        int id = 1;
        for(dk.sdu.compbio.failthmcs.network.Node node : network.vertexSet()) {
            nodeMap.put(node, id++);
            pw.println(String.format("|{%s}|", node.getLabel()));
        }

        pw.println(Integer.toString(network.edgeSet().size()));
        for(Edge edge : network.edgeSet()) {
            int i = nodeMap.get(edge.getSource());
            int j = nodeMap.get(edge.getTarget());
            pw.println(String.format("%d %d 0 |{}|", i, j));
        }

        pw.close();
    }
}
