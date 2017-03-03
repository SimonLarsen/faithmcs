package dk.sdu.compbio.failthmcs.network.io;

import dk.sdu.compbio.failthmcs.network.Edge;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class SIFImporter implements Importer {
    @Override
    public void read(dk.sdu.compbio.failthmcs.network.Network network, File file) throws FileNotFoundException {
        Map<String, dk.sdu.compbio.failthmcs.network.Node> nodeMap = new HashMap<>();

        Scanner scan = new Scanner(file);
        while(scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            String[] parts = line.split("\t");
            if(parts.length == 0) continue;
            dk.sdu.compbio.failthmcs.network.Node source = getNode(parts[0], nodeMap, network);
            if(parts.length >= 3) {
                for(int i = 2; i < parts.length; ++i) {
                    dk.sdu.compbio.failthmcs.network.Node target = getNode(parts[i], nodeMap, network);
                    network.addEdge(source, target, new Edge(source, target));
                }
            }
        }
        scan.close();
    }

    private static dk.sdu.compbio.failthmcs.network.Node getNode(String label, Map<String, dk.sdu.compbio.failthmcs.network.Node> nodeMap, dk.sdu.compbio.failthmcs.network.Network network) {
        dk.sdu.compbio.failthmcs.network.Node node = nodeMap.get(label);
        if(node == null) {
            node = new dk.sdu.compbio.failthmcs.network.Node(label);
            network.addVertex(node);
            nodeMap.put(label, node);
        }
        return node;
    }
}
