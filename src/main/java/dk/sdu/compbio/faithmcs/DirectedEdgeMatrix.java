package dk.sdu.compbio.faithmcs;

import dk.sdu.compbio.faithmcs.network.DirectedNetwork;
import dk.sdu.compbio.faithmcs.network.Edge;

import java.util.List;

public class DirectedEdgeMatrix implements EdgeMatrix {
    private final int n, M;
    private final int[][] edges;

    public DirectedEdgeMatrix(int n, int M) {
        this.n = n;
        this.M = M;
        this.edges = new int[M][M];
    }

    public DirectedEdgeMatrix(List<DirectedNetwork> networks) {
        this.n = networks.size();
        this.M = networks.stream().mapToInt(v -> v.vertexSet().size()).max().getAsInt();
        this.edges = new int[M][M];
        for(DirectedNetwork network : networks) {
            for(Edge e : network.edgeSet()) {
                int i = e.getSource().getPosition();
                int j = e.getTarget().getPosition();
                increment(i, j);
            }
        }
    }

    @Override
    public int countEdges() {
        int count = 0;
        for(int i = 0; i < M; ++i) {
            for(int j = 0; j < M; ++j) {
                if(get(i, j) == n) count++;
            }
        }
        return count;
    }

    @Override
    public int size() { return M; }

    @Override
    public int get(int i, int j) {
        return edges[i][j];
    }

    @Override
    public void set(int i, int j, int value) {
        edges[i][j] = value;
    }

    @Override
    public void increment(int i, int j) {
        edges[i][j]++;
    }

    @Override
    public void decrement(int i, int j) {
        edges[i][j]--;
    }
}
