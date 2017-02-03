package dk.sdu.compbio.netgale.network.io;

import dk.sdu.compbio.netgale.network.Edge;
import dk.sdu.compbio.netgale.network.Network;
import dk.sdu.compbio.netgale.network.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class SIFImporter implements Importer {
    @Override
    public void read(Network network, File file) throws FileNotFoundException {
        Map<String,Node> nodeMap = new HashMap<>();

        Scanner scan = new Scanner(file);
        while(scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            String[] parts = line.split("\t");
            if(parts.length == 0) continue;
            Node source = getNode(parts[0], nodeMap, network);
            if(parts.length >= 3) {
                for(int i = 2; i < parts.length; ++i) {
                    Node target = getNode(parts[i], nodeMap, network);
                    network.addEdge(source, target, new Edge(source, target));
                }
            }
        }
        scan.close();
    }

    private static Node getNode(String label, Map<String,Node> nodeMap, Network network) {
        Node node = nodeMap.get(label);
        if(node == null) {
            node = new Node(label);
            network.addVertex(node);
            nodeMap.put(label, node);
        }
        return node;
    }
}
