package dk.sdu.compbio.netgale.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SIFImporter {
    public static void read(Network network, File file) throws FileNotFoundException {
        Scanner scan = new Scanner(file);
        while(scan.hasNextLine()) {
            String line = scan.nextLine();
            String[] parts = line.split("\t");
            if(parts.length == 0) continue;
            Node source = new Node(parts[0]);
            network.addVertex(source);
            if(parts.length >= 3) {
                for(int i = 2; i < parts.length; ++i) {
                    Node target = new Node(parts[i]);
                    network.addVertex(target);
                    network.addEdge(source, target, new Edge(source, target));
                }
            }
        }
        scan.close();
    }
}
