package dk.sdu.compbio.netgale;

import com.google.common.collect.Sets;
import dk.sdu.compbio.netgale.network.Edge;
import dk.sdu.compbio.netgale.network.Network;
import dk.sdu.compbio.netgale.network.Node;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        List<Map<Node,Integer>> nodeMaps = new ArrayList<>();
        List<Map<Integer,Node>> revNodeMaps = new ArrayList<>();
        for(Network network : networks) {
            Map<Node,Integer> map = new HashMap<>();
            Map<Integer,Node> revMap = new HashMap<>();
            int id = 0;
            for(Node node : network.vertexSet()) {
                int nid = id++;
                map.put(node, nid);
                revMap.put(nid, node);
            }
            while(map.size() < M) {
                Node fake_node = new Node("fake$" + id);
                network.addVertex(fake_node);
                int nid = id++;
                map.put(fake_node, nid);
                revMap.put(nid, fake_node);

            }
            nodeMaps.add(map);
            revNodeMaps.add(revMap);
        }

        List<List<Set<Integer>>> neighbors = new ArrayList<>();
        for(int i = 0; i < n; ++i) {
            List<Set<Integer>> nneighbors = new ArrayList<>();
            for(int j = 0; j < M; ++j) {
                nneighbors.add(new TreeSet<>());
            }

            for(Edge e : networks.get(i).edgeSet()) {
                int j = nodeMaps.get(i).get(e.getSource());
                int k = nodeMaps.get(i).get(e.getTarget());
                nneighbors.get(j).add(k);
                nneighbors.get(k).add(j);
            }

            neighbors.add(nneighbors);
        }

        int[][] edges = new int[M][M];
        for(int i = 0; i < n; ++i) {
            for(int j = 0; j < M; ++j) {
                for(Integer k : neighbors.get(i).get(j)) {
                    edges[j][k]++;
                }
            }
        }

        int[][] alignment = new int[n][M];
        for(int i = 0; i < n; ++i) {
            for(int j = 0; j < M; ++j) {
                alignment[i][j] = j;
            }
        }

        Random rand = new Random();
        for(int iteration = 0; iteration < max_iterations; ++iteration) {
            int i = rand.nextInt(n);
            int j = rand.nextInt(M);
            int k = j;
            while(k == j) k = rand.nextInt(M);
            float dt = delta(alignment, edges, neighbors, i, j, k);
            if(dt > 0f) {
                swap(alignment, edges, neighbors, i, j, k);
            }
        }

        List<List<Node>> nalignment = new ArrayList<>();
        for(int i = 0; i < n; ++i) {
            List<Node> row = new ArrayList<>();
            for(int j = 0; j < M; ++j) {
                row.add(revNodeMaps.get(i).get(alignment[i][j]));
            }
            nalignment.add(row);
        }

        return new Alignment(nalignment);

        /*
        for(int iteration = 0; iteration < max_iterations; ++iteration) {
            float temperature = (1.0f - iteration / max_iterations) * start_temperature;
        }
        */
    }

    private float delta(int[][] alignment, int[][] edges, List<List<Set<Integer>>> neighbors, int i, int j, int k) {
        float delta = 0;

        Integer u = alignment[i][j];
        Integer v = alignment[i][k];

        Sets.SetView<Integer> diff = Sets.difference(neighbors.get(i).get(u), neighbors.get(i).get(v));
        for(Integer w : diff) {
            if(w != v) {
                delta += -Math.pow(edges[u][w], model.getAlpha()) + Math.pow(edges[u][w]-1, model.getAlpha());
                delta += -Math.pow(edges[v][w], model.getAlpha()) + Math.pow(edges[v][w]+1, model.getAlpha());
            }
        }

        diff = Sets.difference(neighbors.get(i).get(v), neighbors.get(i).get(u));
        for(Integer w : diff) {
            if(w != u) {
                delta += -Math.pow(edges[v][w], model.getAlpha()) + Math.pow(edges[v][w]-1, model.getAlpha());
                delta += -Math.pow(edges[u][w], model.getAlpha()) + Math.pow(edges[u][w]+1, model.getAlpha());
            }
        }

        return delta;
    }

    private void swap(int[][] alignment, int[][] edges, List<List<Set<Integer>>> neighbors, int i, int j, int k) {
        Integer u = alignment[i][j];
        Integer v = alignment[i][k];

        Sets.SetView<Integer> diff = Sets.difference(neighbors.get(i).get(u), neighbors.get(i).get(v));
        for(Integer w : diff) {
            if(w != v) {
                edges[u][w]--;
                edges[v][w]++;
            }
        }

        diff = Sets.difference(neighbors.get(i).get(v), neighbors.get(i).get(u));
        for(Integer w : diff) {
            if(w != u) {
                edges[v][w]--;
                edges[u][w]++;
            }
        }

        alignment[i][j] = v;
        alignment[i][k] = u;
    }
}
