package dk.sdu.compbio.netgale.alg;

import com.google.common.collect.Sets;
import dk.sdu.compbio.netgale.Alignment;
import dk.sdu.compbio.netgale.Model;
import dk.sdu.compbio.netgale.alg.Aligner;
import dk.sdu.compbio.netgale.network.Edge;
import dk.sdu.compbio.netgale.network.Network;
import dk.sdu.compbio.netgale.network.Node;
import org.jgrapht.alg.NeighborIndex;

import java.util.*;
import java.util.stream.Collectors;

public class SimulatedAnnealingAligner implements Aligner {
    private final Model model;
    private final float start_temperature;
    private final int max_iterations;

    public SimulatedAnnealingAligner(Model model, float start_temperature, int max_iterations) {
        this.model = model;
        this.start_temperature = start_temperature;
        this.max_iterations = max_iterations;
    }

    @Override
    public Alignment align(List<Network> networks, Model model) {
        int n = networks.size();
        int m = networks.stream().mapToInt(v -> v.vertexSet().size()).min().getAsInt();
        int M = networks.stream().mapToInt(v -> v.vertexSet().size()).max().getAsInt();

        List<NeighborIndex<Node,Edge>> indices = new ArrayList<>();

        for(Network network : networks) {
            int fid = 0;
            while(network.vertexSet().size() < M) {
                Node fake_node = new Node("$fake$" + fid++, true);
                network.addVertex(fake_node);
            }

            int pos = 0;
            for(Node node : network.vertexSet()) {
                node.setPosition(pos++);
            }

            indices.add(new NeighborIndex<>(network));
        }

        int[][] edges = new int[M][M];
        for(Network network : networks) {
            for(Edge e : network.edgeSet()) {
                int i = e.getSource().getPosition();
                int j = e.getTarget().getPosition();
                edges[i][j]++;
                edges[j][i]++;
            }
        }

        List<List<Node>> nodes = networks.stream().map(network -> network.vertexSet().stream().collect(Collectors.toList())).collect(Collectors.toList());

        Random rand = new Random();
        for(int iteration = 0; iteration < max_iterations; ++iteration) {
            float temperature = (1.0f - iteration / max_iterations) * start_temperature;

            int i = rand.nextInt(n);
            int j = rand.nextInt(M);
            int k;
            do {
                k = rand.nextInt(M);
            } while(j == k);

            float dt = delta(edges, indices.get(i), nodes.get(i).get(j), nodes.get(i).get(k));
            if(dt >= 0f || rand.nextFloat() < Math.exp(dt / temperature)) {
                swap(edges, indices.get(i), nodes.get(i).get(j), nodes.get(i).get(k));
            }
        }

        // Sort nodes on position to obtain alignment
        for(List<Node> node_list : nodes) {
            node_list.sort(Comparator.comparingInt(Node::getPosition));
        }

        return new Alignment(nodes, networks);
    }

    private float delta(int[][] edges, NeighborIndex<Node,Edge> index, Node u, Node v) {
        float delta = 0;

        int i = u.getPosition();
        int j = v.getPosition();

        for(Node w : Sets.difference(index.neighborsOf(u), index.neighborsOf(v))){
            if(w != v) {
                int l = w.getPosition();
                delta -= 2 * edges[i][l] - 1;
                delta += 2 * edges[j][l] + 1;
            }
        }

        for(Node w : Sets.difference(index.neighborsOf(v), index.neighborsOf(u))) {
            if(w != u) {
                int l = w.getPosition();
                delta -= 2 * edges[j][l] - 1;
                delta += 2 * edges[i][l] + 1;
            }
        }

        return delta;
    }

    private void swap(int[][] edges, NeighborIndex<Node,Edge> index, Node u, Node v) {
        int i = u.getPosition();
        int j = v.getPosition();

        for(Node w : Sets.difference(index.neighborsOf(u), index.neighborsOf(v))) {
            if(w != v) {
                int l = w.getPosition();
                edges[i][l]--;
                edges[l][i]--;
                edges[j][l]++;
                edges[l][j]++;
            }
        }

        for(Node w : Sets.difference(index.neighborsOf(v), index.neighborsOf(u))) {
            if(w != u) {
                int l = w.getPosition();
                edges[j][l]--;
                edges[l][j]--;
                edges[i][l]++;
                edges[l][i]++;
            }
        }

        u.setPosition(j);
        v.setPosition(i);
    }
}
