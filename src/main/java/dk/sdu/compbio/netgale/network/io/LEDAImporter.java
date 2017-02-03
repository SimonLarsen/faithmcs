package dk.sdu.compbio.netgale.network.io;

import dk.sdu.compbio.netgale.network.Edge;
import dk.sdu.compbio.netgale.network.Network;
import dk.sdu.compbio.netgale.network.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LEDAImporter implements Importer {
    @Override
    public void read(Network network, File file) throws FileNotFoundException, ImportException {
        Scanner scan = new Scanner(file);

        String line = readLine(scan);
        if(!line.equals("LEDA.GRAPH")) throw new ImportException("Invalid header in LEDA file");

        readLine(scan); // vertex type
        readLine(scan); // edge type

        line = readLine(scan); // directed (-1) or undirected (-2) ?
        if(line.equals("-1")) System.err.println("warning: Imported directed network as undirected.");

        int num_nodes = Integer.parseInt(readLine(scan));
        List<Node> nodes = new ArrayList<>();
        for(int i = 0; i < num_nodes; ++i) {
            line = readLine(scan);
            int start = line.indexOf('{')+1;
            int end = line.lastIndexOf('}');
            String label = line.substring(start, end);
            Node node = new Node(label);
            network.addVertex(node);
            nodes.add(node);
        }

        int num_edges = Integer.parseInt(readLine(scan));
        for(int i = 0; i < num_edges; ++i) {
            line = readLine(scan);
            String[] parts = line.split(" ");
            if(parts.length != 4) throw new ImportException("Invalid edge declaration.");
            int j = Integer.parseInt(parts[0]);
            int k = Integer.parseInt(parts[1]);

            Node source = nodes.get(j-1);
            Node target = nodes.get(k-1);
            Edge edge = new Edge(source, target);
            network.addEdge(source, target, edge);
        }

        scan.close();
    }

    private String readLine(Scanner scan) throws ImportException {
        String line;
        do {
            if(!scan.hasNextLine()) throw new ImportException("Reached end of file while parsing.");
            line = scan.nextLine().trim();
        } while(line.length() == 0 || line.startsWith("#"));

        return line;
    }
}
