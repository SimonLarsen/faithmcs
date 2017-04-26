package dk.sdu.compbio.faithmcs;

import dk.sdu.compbio.faithmcs.network.Edge;
import dk.sdu.compbio.faithmcs.network.UndirectedNetwork;
import dk.sdu.compbio.faithmcs.network.Node;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UndirectedAlignment implements Alignment<Node,Edge> {
    private final List<List<Node>> alignment;
    private final List<UndirectedNetwork> networks;

    public UndirectedAlignment(List<List<Node>> nodes, List<UndirectedNetwork> networks) {
        this.alignment = nodes;
        this.networks = networks;
    }

    @Override
    public List<List<Node>> getAlignment() {
        return alignment;
    }

    @Override
    public UndirectedNetwork buildNetwork(int exceptions, boolean remove_leaf_exceptions) {
        int M = alignment.get(0).size();

        int[][] edges = new int[M][M];
        for(UndirectedNetwork network : networks) {
            for(Edge e : network.edgeSet()) {
                int i = e.getSource().getPosition();
                int j = e.getTarget().getPosition();
                edges[i][j]++;
                edges[j][i]++;
            }
        }

        UndirectedNetwork network = new UndirectedNetwork();
        List<Node> nodes = new ArrayList<>();
        for(int i = 0; i < M; ++i) {
            int finalI = i;
            String label = IntStream.range(0, networks.size())
                    .mapToObj(j -> this.alignment.get(j).get(finalI))
                    .map(Node::getLabel)
                    .collect(Collectors.joining(","));
            boolean fake = IntStream.range(0, networks.size())
                    .anyMatch(j -> this.alignment.get(j).get(finalI).isFake());
            Node node = new Node(label, fake);
            node.setPosition(i);
            nodes.add(node);
            network.addVertex(node);
        }

        for(int i = 0; i < M; ++i) {
            for(int j = i+1; j < M; ++j) {
                if(edges[i][j] >= networks.size() - exceptions) {
                    int finalI = i;
                    int finalJ = j;
                    String label = IntStream.range(0, networks.size())
                            .mapToObj(g -> networks.get(g).getEdge(this.alignment.get(g).get(finalI), this.alignment.get(g).get(finalJ)))
                            .filter(Objects::nonNull)
                            .map(Edge::getLabel)
                            .collect(Collectors.joining(","));
                    network.addEdge(nodes.get(i), nodes.get(j), new Edge(nodes.get(i), nodes.get(j), label, edges[i][j]));
                }
            }
        }

        if(remove_leaf_exceptions) {
            Set<Node> remove_nodes = new HashSet<>();
            for(Edge e : network.edgeSet()) {
                if(e.getConservation() < networks.size()) {
                    if(network.degreeOf(e.getSource()) == 1) remove_nodes.add(e.getSource());
                    if(network.degreeOf(e.getTarget()) == 1) remove_nodes.add(e.getTarget());
                }
            }
            network.removeAllVertices(remove_nodes);
        }

        return network;
    }
}
