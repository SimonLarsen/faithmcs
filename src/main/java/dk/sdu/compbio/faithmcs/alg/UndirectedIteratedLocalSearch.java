package dk.sdu.compbio.faithmcs.alg;

import com.google.common.collect.Sets;
import dk.sdu.compbio.faithmcs.UndirectedAlignment;
import dk.sdu.compbio.faithmcs.UndirectedEdgeMatrix;
import dk.sdu.compbio.faithmcs.network.Edge;
import dk.sdu.compbio.faithmcs.network.UndirectedNetwork;
import dk.sdu.compbio.faithmcs.network.Node;
import org.jgrapht.alg.NeighborIndex;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UndirectedIteratedLocalSearch implements IteratedLocalSearch {
    private final int n, M;
    private final List<UndirectedNetwork> networks;
    private float perturbation_amount;

    private final List<NeighborIndex<Node,Edge>> indices;
    private final List<List<Node>> nodes;
    private final UndirectedEdgeMatrix edges;
    private final int[][] best_positions;
    private int quality, best_quality;
    private final Random rand;

    public UndirectedIteratedLocalSearch(List<UndirectedNetwork> networks, float perturbation_amount) {
        this.networks = networks;
        this.perturbation_amount = perturbation_amount;

        n = networks.size();
        M = networks.stream().mapToInt(v -> v.vertexSet().size()).max().getAsInt();

        indices = new ArrayList<>();

        int fid = 0;
        for(UndirectedNetwork network : networks) {
            while(network.vertexSet().size() < M) {
                Node fake_node = new Node("$fake$" + fid++, true);
                network.addVertex(fake_node);
            }

            indices.add(new NeighborIndex<>(network));
        }

        nodes = networks.stream()
                .map(network -> new ArrayList<>(network.vertexSet()))
                .collect(Collectors.toList());

        for(int i = 0; i < n; ++i) {
            nodes.get(i).sort(Comparator.comparingInt(networks.get(i)::degreeOf).reversed());
            int pos = 0;
            for(Node node : nodes.get(i)) {
                node.setPosition(pos++);
            }
        }

        edges = new UndirectedEdgeMatrix(networks);
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
                swap(indices.get(i), nodes.get(i).get(j), nodes.get(i).get(k));
            }
        }

        // local search step
        boolean repeat = true;
        while(repeat) {
            repeat = false;
            for (int i = 1; i < n; ++i) {
                for (int j = 0; j < M-1; ++j) {
                    int finalI = i;
                    int finalJ = j;

                    List<Integer> dts = IntStream.range(j+1, M)
                            .parallel()
                            .mapToObj(k -> delta(indices.get(finalI), nodes.get(finalI).get(finalJ), nodes.get(finalI).get(k)))
                            .collect(Collectors.toList());

                    Integer best = IntStream.range(j+1, M)
                            .parallel()
                            .boxed()
                            .max(Comparator.comparingInt(k -> dts.get(k-(finalJ+1)))).get();

                    int dt = dts.get(best-(j+1));

                    if(dt > 0) {
                        repeat = true;
                        swap(indices.get(i), nodes.get(i).get(j), nodes.get(i).get(best));
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

    private int delta(NeighborIndex<Node,Edge> index, Node u, Node v) {
        int delta = 0;

        int i = u.getPosition();
        int j = v.getPosition();

        for(Node w : Sets.difference(index.neighborsOf(u), index.neighborsOf(v))){
            if(w != v) {
                int l = w.getPosition();
                delta -= 2 * edges.get(i, l) - 1;
                delta += 2 * edges.get(j, l) + 1;
            }
        }

        for(Node w : Sets.difference(index.neighborsOf(v), index.neighborsOf(u))) {
            if(w != u) {
                int l = w.getPosition();
                delta -= 2 * edges.get(j, l) - 1;
                delta += 2 * edges.get(i, l) + 1;
            }
        }

        return delta;
    }

    private void swap(NeighborIndex<Node,Edge> index, Node u, Node v) {
        int i = u.getPosition();
        int j = v.getPosition();

        for(Node w : Sets.difference(index.neighborsOf(u), index.neighborsOf(v))) {
            if(w != v) {
                int l = w.getPosition();
                edges.decrement(i, l);
                edges.increment(j, l);
            }
        }

        for(Node w : Sets.difference(index.neighborsOf(v), index.neighborsOf(u))) {
            if(w != u) {
                int l = w.getPosition();
                edges.decrement(j, l);
                edges.increment(i, l);
            }
        }

        u.setPosition(j);
        v.setPosition(i);
    }

    @Override
    public UndirectedAlignment getAlignment() {
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

        return new UndirectedAlignment(nodes, networks);
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
