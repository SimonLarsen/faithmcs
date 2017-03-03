package dk.sdu.compbio.failthmcs.network.io;

import dk.sdu.compbio.failthmcs.network.Edge;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LEDAImporter implements Importer {
    @Override
    public void read(dk.sdu.compbio.failthmcs.network.Network network, File file) throws FileNotFoundException, dk.sdu.compbio.failthmcs.network.io.ImportException {
        Scanner scan = new Scanner(file);

        String line = readLine(scan);
        if(!line.equals("LEDA.GRAPH")) throw new dk.sdu.compbio.failthmcs.network.io.ImportException("Invalid header in LEDA file");

        readLine(scan); // vertex type
        readLine(scan); // edge type

        line = readLine(scan); // directed (-1) or undirected (-2) ?
        if(line.equals("-1")) System.err.println("warning: Imported directed network as undirected.");

        int num_nodes = Integer.parseInt(readLine(scan));
        List<dk.sdu.compbio.failthmcs.network.Node> nodes = new ArrayList<>();
        for(int i = 0; i < num_nodes; ++i) {
            line = readLine(scan);
            int start = line.indexOf('{')+1;
            int end = line.lastIndexOf('}');
            String label = line.substring(start, end);
            dk.sdu.compbio.failthmcs.network.Node node = new dk.sdu.compbio.failthmcs.network.Node(label);
            network.addVertex(node);
            nodes.add(node);
        }

        int num_edges = Integer.parseInt(readLine(scan));
        for(int i = 0; i < num_edges; ++i) {
            line = readLine(scan);
            String[] parts = line.split(" ");
            if(parts.length != 4) throw new dk.sdu.compbio.failthmcs.network.io.ImportException("Invalid edge declaration.");
            int j = Integer.parseInt(parts[0]);
            int k = Integer.parseInt(parts[1]);

            dk.sdu.compbio.failthmcs.network.Node source = nodes.get(j-1);
            dk.sdu.compbio.failthmcs.network.Node target = nodes.get(k-1);
            Edge edge = new Edge(source, target);
            network.addEdge(source, target, edge);
        }

        scan.close();
    }

    private String readLine(Scanner scan) throws dk.sdu.compbio.failthmcs.network.io.ImportException {
        String line;
        do {
            if(!scan.hasNextLine()) throw new dk.sdu.compbio.failthmcs.network.io.ImportException("Reached end of file while parsing.");
            line = scan.nextLine().trim();
        } while(line.length() == 0 || line.startsWith("#"));

        return line;
    }
}
