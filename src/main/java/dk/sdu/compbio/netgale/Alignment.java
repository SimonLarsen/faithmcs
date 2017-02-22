package dk.sdu.compbio.netgale;

import dk.sdu.compbio.netgale.network.Edge;
import dk.sdu.compbio.netgale.network.Network;
import dk.sdu.compbio.netgale.network.Node;
import org.jgrapht.alg.ConnectivityInspector;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Alignment {
    private final List<List<Node>> alignment;
    private final List<Network> networks;

    public Alignment(List<List<Node>> alignment, List<Network> networks) {
        this.alignment = alignment;
        this.networks = networks;
    }

    public List<List<Node>> getAlignment() {
        return alignment;
    }

    public List<Network> getNetworks() { return networks; }

    public Network buildNetwork(int exceptions, boolean connected) {
        int M = alignment.get(0).size();

        int[][] edges = new int[M][M];
        for(Network network : networks) {
            for(Edge e : network.edgeSet()) {
                int i = e.getSource().getPosition();
                int j = e.getTarget().getPosition();
                edges[i][j]++;
                edges[j][i]++;
            }
        }

        Network network = new Network();
        List<Node> nodes = new ArrayList<>();
        for(int i = 0; i < M; ++i) {
            int finalI = i;
            String label = IntStream.range(0, networks.size()).mapToObj(j -> alignment.get(j).get(finalI).getLabel()).collect(Collectors.joining("-"));
            boolean fake = IntStream.range(0, networks.size()).anyMatch(j -> alignment.get(j).get(finalI).isFake());
            Node node = new Node(label, fake);
            nodes.add(node);
            network.addVertex(node);
        }

        for(int i = 0; i < M; ++i) {
            for(int j = i+1; j < M; ++j) {
                if(edges[i][j] >= networks.size() - exceptions && !nodes.get(i).isFake() && !nodes.get(j).isFake()) {
                    network.addEdge(nodes.get(i), nodes.get(j), new Edge(nodes.get(i), nodes.get(j), edges[i][j]));
                }
            }
        }

        if(connected) {
            ConnectivityInspector conn = new ConnectivityInspector(network);
            List<Set<Node>> components = conn.connectedSets();
            Set<Node> largest = components.stream().max(Comparator.comparing(Set::size)).get();
            Set<Node> remove = new HashSet<>();
            components.forEach(s -> {
                if(s != largest) remove.addAll(s);
            });
            network.removeAllVertices(remove);
        }

        return network;
    }
}
