package dk.sdu.compbio.faithmcs;

import dk.sdu.compbio.faithmcs.network.DirectedNetwork;
import dk.sdu.compbio.faithmcs.network.Edge;
import dk.sdu.compbio.faithmcs.network.Node;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DirectedAlignment implements Alignment<Node,Edge> {
    private final List<List<Node>> alignment;
    private final List<DirectedNetwork> networks;

    public DirectedAlignment(List<List<Node>> nodes, List<DirectedNetwork> networks) {
        this.alignment = nodes;
        this.networks = networks;
    }

    @Override
    public List<List<Node>> getAlignment() {
        return alignment;
    }

    @Override
    public DirectedNetwork buildNetwork(int exceptions, boolean remove_leaf_exceptions) {
        int M = alignment.get(0).size();

        int[][] edges = new int[M][M];
        for(DirectedNetwork network : networks) {
            for(Edge e : network.edgeSet()) {
                int i = e.getSource().getPosition();
                int j = e.getTarget().getPosition();
                edges[i][j]++;
            }
        }

        DirectedNetwork network = new DirectedNetwork();
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
            for(int j = 0; j < M; ++j) {
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
                    if(network.inDegreeOf(e.getSource()) + network.outDegreeOf(e.getSource()) == 1) remove_nodes.add(e.getSource());
                    if(network.inDegreeOf(e.getTarget()) + network.outDegreeOf(e.getTarget()) == 1) remove_nodes.add(e.getTarget());
                }
            }
            network.removeAllVertices(remove_nodes);
        }

        return network;
    }
}
