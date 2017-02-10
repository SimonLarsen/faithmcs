package dk.sdu.compbio.netgale.alg;

import com.google.common.collect.Sets;
import dk.sdu.compbio.netgale.network.Edge;
import dk.sdu.compbio.netgale.network.Network;
import dk.sdu.compbio.netgale.network.Node;
import dk.sdu.compbio.netgale.network.io.ImportException;
import org.jgrapht.alg.NeighborIndex;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlignMatrix {
    private final int M;
    private final int[][] edges;
    private final Network network;
    private final NeighborIndex<Node,Edge> index;
    private final List<Node> nodes;
    private final Random rand;

    public AlignMatrix(int[][] edges, Network network) {
        this.edges = edges;
        this.network = network;

        M = Math.max(edges.length, network.vertexSet().size());

        // pad with fake nodes if necessary
        int fid = 0;
        while(network.vertexSet().size() < M) {
            System.err.println("adding fake nodes");
            Node fake_node = new Node("$fake$"+fid++, true);
            network.addVertex(fake_node);
        }

        index = new NeighborIndex<>(network);

        // extend edge matrix to matrix network size
        if(edges.length < M) {
            System.err.println("extending edge matrix");
            int[][] old_edges = edges;
            edges = new int[M][M];
            for(int i = 0; i < old_edges.length; ++i) {
                for(int j = 0; j < old_edges.length; ++j) {
                    edges[i][j] = old_edges[i][j];
                }
            }
        }

        nodes = new ArrayList<>(network.vertexSet());
        for(int i = 0; i < nodes.size(); ++i) {
            nodes.get(i).setPosition(i);
        }

        rand = new Random();
    }

    public void run(int iterations) {
        System.err.println("quality: " + computeQuality());
        for(int iteration = 0; iteration < iterations; ++iteration) {
            step();
            System.err.println("quality: " + computeQuality());
        }
    }

    public void step() {
        for(int rep = 0; rep < M/20; ++rep) {
            int j = rand.nextInt(M);
            int k;
            do k = rand.nextInt(M);
            while(k == j);
            swap(nodes.get(j), nodes.get(k));
        }

        boolean repeat = true;
        while(repeat) {
            repeat = false;
            for(int i = 0; i < M-1; ++i) {
                for(int j = i+1; j < M; ++j) {
                    float dt = delta(nodes.get(i), nodes.get(j));
                    if(dt > 0) {
                        repeat = true;
                        swap(nodes.get(i), nodes.get(j));
                    }
                }
            }
        }
    }

    private float computeQuality() {
        float quality = 0;

        for(Edge e : network.edgeSet()) {
            int i = e.getSource().getPosition();
            int j = e.getTarget().getPosition();

            quality += edges[i][j];
        }

        return quality;
    }

    private float delta(Node u, Node v) {
        float delta = 0;

        int i = u.getPosition();
        int j = v.getPosition();

        for(Node w : Sets.difference(index.neighborsOf(u), index.neighborsOf(v))) {
            if(w != v) {
                int l = w.getPosition();
                delta -= edges[i][l];
                delta += edges[j][l];
            }
        }

        for(Node w : Sets.difference(index.neighborsOf(v), index.neighborsOf(u))) {
            if(w != u) {
                int l = w.getPosition();
                delta -= edges[j][l];
                delta += edges[i][l];
            }
        }

        return delta;
    }

    private void swap(Node u, Node v) {
        int i = u.getPosition();
        int j = v.getPosition();

        u.setPosition(j);
        v.setPosition(i);
    }

    public static void main(String[] args) throws FileNotFoundException, ImportException {
        /*
        System.err.println("reading edges");
        int[][] edges = EdgeMatrix.fromFile(new File(args[0]));
        System.err.println("reading network");
        Network network = new Network();
        NetworkReader.read(network, new File(args[1]));

        System.err.println("creating aligner");
        AlignMatrix aligner = new AlignMatrix(edges, network);
        aligner.run(Integer.parseInt(args[2]));
        */
    }
}
