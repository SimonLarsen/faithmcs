package dk.sdu.compbio.netgale.alg;

import dk.sdu.compbio.netgale.network.Edge;
import dk.sdu.compbio.netgale.network.Network;

import java.util.List;

public class EdgeMatrix {
    public static int[][] compute(List<Network> networks) {
        int M = networks.stream().mapToInt(v -> v.vertexSet().size()).max().getAsInt();
        int[][] edges = new int[M][M];
        compute(networks, edges);
        return edges;
    }

    public static void compute(List<Network> networks, int[][] edges) {
        for(Network network : networks) {
            for(Edge e : network.edgeSet()) {
                int i = e.getSource().getPosition();
                int j = e.getTarget().getPosition();
                edges[i][j]++;
                edges[j][i]++;
            }
        }
    }
}
