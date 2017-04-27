package dk.sdu.compbio.faithmcs.alg;

import com.google.common.collect.Sets;
import dk.sdu.compbio.faithmcs.DirectedAlignment;
import dk.sdu.compbio.faithmcs.DirectedEdgeMatrix;
import dk.sdu.compbio.faithmcs.network.DirectedNetwork;
import dk.sdu.compbio.faithmcs.network.Edge;
import dk.sdu.compbio.faithmcs.network.Node;
import org.jgrapht.alg.DirectedNeighborIndex;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DirectedIteratedLocalSearch implements IteratedLocalSearch {
    private final int n, M;
    private final List<DirectedNetwork> networks;
    private float perturbation_amount;

    private final List<DirectedNeighborIndex<Node,Edge>> indices;
    private final List<List<Node>> nodes;
    private final DirectedEdgeMatrix edges;
    private final int[][] best_positions;
    private int quality, best_quality;
    private final Random rand;

    public DirectedIteratedLocalSearch(List<DirectedNetwork> networks, float perturbation_amount) {
        this.networks = networks;
        this.perturbation_amount = perturbation_amount;

        n = networks.size();
        M = networks.stream().mapToInt(v -> v.vertexSet().size()).max().getAsInt();

        indices = new ArrayList<>();

        int fid = 0;
        for(DirectedNetwork network : networks) {
            while(network.vertexSet().size() < M) {
                Node fake_node = new Node("$fake$" + fid++, true);
                network.addVertex(fake_node);
            }

            indices.add(new DirectedNeighborIndex<>(network));
        }

        nodes = networks.stream()
                .map(network -> network.vertexSet().stream().collect(Collectors.toList()))
                .collect(Collectors.toList());

        for(int i = 0; i < n; ++i) {
            nodes.get(i).sort(Comparator.comparingInt(networks.get(i)::outDegreeOf).reversed());
            int pos = 0;
            for(Node node : nodes.get(i)) {
                node.setPosition(pos++);
            }
        }

        edges = new DirectedEdgeMatrix(networks);
        rand = new Random();

        best_positions = new int[n][M];
        copyPositions(nodes, best_positions);
        best_quality = edges.countEdges();
    }

    @Override
    public void run(int max_nonimproving) {
        int nonimproving = 0;
        while(nonimproving < max_nonimproving) {
            nonimproving++;
            if(step()) {
                nonimproving = 0;
            }
            System.err.println(String.format("current: %d edges, best: %d edges", quality, best_quality));
        }
    }

    @Override
    public boolean step() {
        // perturbation step
        int count = Math.round(M * perturbation_amount);
        for(int i = 1; i < n; ++i) {
            for(int rep = 0; rep < count; ++rep) {
                int j = rand.nextInt(M);
                int k;
                do k = rand.nextInt(M); while(k == j);
                swap(networks.get(i), indices.get(i), nodes.get(i).get(j), nodes.get(i).get(k));
            }
        }

        // local search step
        boolean repeat = true;
        while(repeat) {
            repeat = false;
            for(int i = 1; i < n; ++i) {
                for(int j = 0; j < M-1; ++j) {
                    int finalI = i;
                    int finalJ = j;

                    List<Integer> dts = IntStream.range(j+1, M)
                            .parallel()
                            .mapToObj(k -> delta(networks.get(finalI), indices.get(finalI), nodes.get(finalI).get(finalJ), nodes.get(finalI).get(k)))
                            .collect(Collectors.toList());

                    Integer best = IntStream.range(j+1, M)
                            .parallel()
                            .mapToObj(v -> v)
                            .max(Comparator.comparingInt(k -> dts.get(k-(finalJ+1)))).get();

                    int dt = dts.get(best-(j+1));

                    if(dt > 0) {
                        repeat = true;
                        swap(networks.get(i), indices.get(i), nodes.get(i).get(j), nodes.get(i).get(best));
                    }
                }
            }
        }

        // count edges
        quality = edges.countEdges();
        if(quality > best_quality) {
            best_quality = quality;
            copyPositions(nodes, best_positions);
            return true;
        }
        return false;
    }

    private void copyPositions(List<List<Node>> nodes, int[][] positions) {
        for(int i = 0; i < nodes.size(); ++i) {
            for(int j = 0; j < nodes.get(i).size(); ++j) {
                positions[i][j] = nodes.get(i).get(j).getPosition();
            }
        }
    }

    private int delta(DirectedNetwork network, DirectedNeighborIndex<Node,Edge> index, Node u, Node v) {
        int delta = 0;

        int i = u.getPosition();
        int j = v.getPosition();

        for(Node w : Sets.difference(index.successorsOf(u), index.successorsOf(v))) {
            if(w != v) {
                int l = w.getPosition();
                delta -= 2 * edges.get(i, l) - 1;
                delta += 2 * edges.get(j, l) + 1;
            }
        }

        for(Node w : Sets.difference(index.successorsOf(v), index.successorsOf(u))) {
            if(w != u) {
                int l = w.getPosition();
                delta -= 2 * edges.get(j, l) - 1;
                delta += 2 * edges.get(i, l) + 1;
            }
        }

        for(Node w : Sets.difference(index.predecessorsOf(u), index.predecessorsOf(v))) {
            if(w != v) {
                int l = w.getPosition();
                delta -= 2 * edges.get(l, i) - 1;
                delta += 2 * edges.get(l, j) + 1;
            }
        }

        for(Node w : Sets.difference(index.predecessorsOf(v), index.predecessorsOf(u))) {
            if(w != u) {
                int l = w.getPosition();
                delta -= 2 * edges.get(l, j) - 1;
                delta += 2 * edges.get(l, i) + 1;
            }
        }

        boolean has_uv = network.containsEdge(u, v);
        boolean has_vu = network.containsEdge(v, u);

        if(has_uv && !has_vu) {
            delta -= 2 * edges.get(i, j) - 1;
            delta += 2 * edges.get(j, i) + 1;
        }
        if(!has_uv && has_vu) {
            delta -= 2 * edges.get(j, i) - 1;
            delta += 2 * edges.get(i, j) + 1;
        }

        return delta;
    }

    private void swap(DirectedNetwork network, DirectedNeighborIndex<Node,Edge> index, Node u, Node v) {
        int i = u.getPosition();
        int j = v.getPosition();

        for(Node w : Sets.difference(index.successorsOf(u), index.successorsOf(v))) {
            if(w != v) {
                int l = w.getPosition();
                edges.decrement(i, l);
                edges.increment(j, l);
            }
        }

        for(Node w : Sets.difference(index.successorsOf(v), index.successorsOf(u))) {
            if(w != u) {
                int l = w.getPosition();
                edges.decrement(j, l);
                edges.increment(i, l);
            }
        }

        for(Node w : Sets.difference(index.predecessorsOf(u), index.predecessorsOf(v))) {
            if(w != v) {
                int l = w.getPosition();
                edges.decrement(l, i);
                edges.increment(l, j);
            }
        }

        for(Node w : Sets.difference(index.predecessorsOf(v), index.predecessorsOf(u))) {
            if(w != u) {
                int l = w.getPosition();
                edges.decrement(l, j);
                edges.increment(l, i);
            }
        }

        boolean has_uv = network.containsEdge(u, v);
        boolean has_vu = network.containsEdge(v, u);

        if(has_uv && !has_vu) {
            edges.decrement(i, j);
            edges.increment(j, i);
        }
        else if(!has_uv && has_vu) {
            edges.decrement(j, i);
            edges.increment(i, j);
        }

        u.setPosition(j);
        v.setPosition(i);
    }

    @Override
    public DirectedAlignment getAlignment() {
        // copy best solution back into nodes
        for(int i = 0; i < n; ++i) {
            for(int j = 0; j < M; ++j) {
                nodes.get(i).get(j).setPosition(best_positions[i][j]);
            }
        }

        // Sort nodes on position to obtain alignment
        for(List<Node> node_list : nodes) {
            node_list.sort(Comparator.comparingInt(Node::getPosition));
        }

        return new DirectedAlignment(nodes, networks);
    }

    @Override
    public int getCurrentNumberOfEdges() {
        return quality;
    }

    @Override
    public int getBestNumberOfEdges() {
        return best_quality;
    }

    @Override
    public void setPerturbationAmount(float a) {
        this.perturbation_amount = a;
    }
}
