package dk.sdu.compbio.netgale.alg;

import com.google.common.collect.Sets;
import dk.sdu.compbio.netgale.alg.Aligner;
import dk.sdu.compbio.netgale.Alignment;
import dk.sdu.compbio.netgale.Model;
import dk.sdu.compbio.netgale.network.Edge;
import dk.sdu.compbio.netgale.network.Network;
import dk.sdu.compbio.netgale.network.Node;
import org.jgrapht.alg.NeighborIndex;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LocalSearch implements Aligner {
    private final Model model;
    private final int max_iterations;

    public LocalSearch(Model model, int max_iterations) {
        this.model = model;
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

            indices.add(new NeighborIndex<>(network));
        }

        List<List<Node>> nodes = networks.stream().map(network -> network.vertexSet().stream().collect(Collectors.toList())).collect(Collectors.toList());
        for(int i = 0; i < n; ++i) {
            nodes.get(i).sort(Comparator.comparingInt(networks.get(i)::degreeOf).reversed());
            int pos = 0;
            for(Node node : nodes.get(i)) {
                node.setPosition(pos++);
            }
        }

        int[][] best_solution = new int[n][M];
        copyPositions(nodes, best_solution);
        int best_quality = 0;

        int[][] edges = new int[M][M];
        for(Network network : networks) {
            for(Edge e : network.edgeSet()) {
                int i = e.getSource().getPosition();
                int j = e.getTarget().getPosition();
                edges[i][j]++;
                edges[j][i]++;
            }
        }

        Random rand = new Random();
        for(int iteration = 0; iteration < max_iterations; ++iteration) {
            for(int i = 1; i < n; ++i) {
                for(int rep = 0; rep < M/10; ++rep) {
                    int j = rand.nextInt(M);
                    int k;
                    do k = rand.nextInt(M);
                    while(k == j);
                    swap(edges, indices.get(i), nodes.get(i).get(j), nodes.get(i).get(k));
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

                        List<Double> dts = IntStream.range(j+1, M).parallel().mapToObj(k -> {
                            return new Double(delta(edges, indices.get(finalI), nodes.get(finalI).get(finalJ), nodes.get(finalI).get(k)));
                        }).collect(Collectors.toList());

                        Integer best = IntStream.range(j+1, M).parallel().mapToObj(v -> v).max(Comparator.comparingDouble(k -> dts.get(k-(finalJ+1)))).get();
                        float dt = delta(edges, indices.get(i), nodes.get(i).get(j), nodes.get(i).get(best));

                        if(dt > 0) {
                            repeat = true;
                            swap(edges, indices.get(i), nodes.get(i).get(j), nodes.get(i).get(best));
                        }
                    }
                }
            }

            // count edges
            int quality = countEdges(edges, n);
            if(quality > best_quality) {
                best_quality = quality;
                copyPositions(nodes, best_solution);
            }
            System.err.println(String.format("current: %d edges, best: %d edges", quality, best_quality));
        }

        // copy best solution back into nodes
        for(int i = 0; i < n; ++i) {
            for(int j = 0; j < M; ++j) {
                nodes.get(i).get(j).setPosition(best_solution[i][j]);
            }
        }

        // Sort nodes on position to obtain alignment
        for(List<Node> node_list : nodes) {
            node_list.sort(Comparator.comparingInt(Node::getPosition));
        }

        return new Alignment(nodes, networks);
    }

    private void copyPositions(List<List<Node>> nodes, int[][] positions) {
        for(int i = 0; i < nodes.size(); ++i) {
            for(int j = 0; j < nodes.get(i).size(); ++j) {
                positions[i][j] = nodes.get(i).get(j).getPosition();
            }
        }
    }

    private int countEdges(int[][] edges, int n) {
        int M = edges.length;
        int count = 0;
        for(int j = 0; j < M; ++j) {
            for(int k = j+1; k < M; ++k) {
                if(edges[j][k] == n) count++;
            }
        }
        return count;
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
