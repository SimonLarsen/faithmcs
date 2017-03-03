package dk.sdu.compbio.failthmcs;

import java.util.List;

public class EdgeMatrix {
    private final int n, M;
    private final int edges[];

    public EdgeMatrix(int n, int M) {
        this.n = n;
        this.M = M;
        this.edges = new int[M*(M+1)/2];
    }

    public EdgeMatrix(List<dk.sdu.compbio.failthmcs.network.Network> networks) {
        this.n = networks.size();
        this.M = networks.stream().mapToInt(v -> v.vertexSet().size()).max().getAsInt();
        this.edges = new int[M*(M+1)/2];
        for(dk.sdu.compbio.failthmcs.network.Network network : networks) {
            for(dk.sdu.compbio.failthmcs.network.Edge e : network.edgeSet()) {
                int i = e.getSource().getPosition();
                int j = e.getTarget().getPosition();
                increment(i, j);
            }
        }
    }

    public int countEdges() {
        int count = 0;
        for(int i = 0; i < M-1; ++i) {
            for(int j = i+1; j < M; ++j) {
                if(get(i, j) == n) count++;
            }
        }
        return count;
    }

    public int size() {
        return M;
    }

    public int get(int i, int j) {
        return edges[index(i, j)];
    }

    public void set(int i, int j, int value) {
        edges[index(i, j)] = value;
    }

    public void increment(int i, int j) {
        edges[index(i, j)]++;
    }

    public void decrement(int i, int j) {
        edges[index(i, j)]--;
    }

    private int index(int i, int j) {
        if(i <= j)
            return i * M + j - (i * (i+1) / 2);
        else
            return j * M + i - (j * (j+1) / 2);
    }
}
