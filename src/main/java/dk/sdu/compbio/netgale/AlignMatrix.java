package dk.sdu.compbio.netgale;

import com.google.common.collect.Sets;
import dk.sdu.compbio.netgale.network.Edge;
import dk.sdu.compbio.netgale.network.Network;
import dk.sdu.compbio.netgale.network.Node;
import dk.sdu.compbio.netgale.network.io.ImportException;
import dk.sdu.compbio.netgale.network.io.NetworkReader;
import org.jgrapht.alg.NeighborIndex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlignMatrix {
    private final int M;
    private final ConsensusMatrix motif;
    private final Network network;
    private final int[] best_solution;
    private final NeighborIndex<Node,Edge> index;
    private final List<Node> nodes;
    private final Random rand;

    private float quality, best_quality;

    public AlignMatrix(ConsensusMatrix motif, Network network) {
        this.motif = motif;
        this.network = network;

        M = Math.max(motif.size(), network.vertexSet().size());

        // pad with fake nodes if necessary
        int fid = 0;
        while(network.vertexSet().size() < M) {
            Node fake_node = new Node("$fake$"+fid++, true);
            network.addVertex(fake_node);
        }

        index = new NeighborIndex<>(network);

        // extend edge matrix to matrix network size
        if(motif.size() < M) {
            ConsensusMatrix old_motif = motif;
            motif = new ConsensusMatrix(M);
            for(int i = 0; i < old_motif.size(); ++i) {
                for(int j = 0; j < old_motif.size(); ++j) {
                    motif.set(i, j, old_motif.get(i, j));
                }
            }
        }

        nodes = new ArrayList<>(network.vertexSet());
        for(int i = 0; i < nodes.size(); ++i) {
            nodes.get(i).setPosition(i);
        }

        rand = new Random();

        quality = best_quality = computeQuality();
        best_solution = new int[M];
        copySolution();
    }

    public void run(int iterations) {
        for(int iteration = 0; iteration < iterations; ++iteration) {
            step();
            //System.err.println(String.format("current: %f, best: %f.", quality, best_quality));
        }
        System.err.println("best: " + best_quality + ", M: " + M);
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
                    if(dt > 0.01f) {
                        repeat = true;
                        swap(nodes.get(i), nodes.get(j));
                    }
                }
            }
        }

        quality = computeQuality();
        if(quality > best_quality) {
            best_quality = quality;
            copySolution();
        }
    }

    private void copySolution() {
        for(int i = 0; i < M; ++i) {
            best_solution[i] = nodes.get(i).getPosition();
        }
    }

    private float computeQuality() {
        float quality = 0;

        for(Edge e : network.edgeSet()) {
            int i = e.getSource().getPosition();
            int j = e.getTarget().getPosition();

            quality += Math.pow(motif.get(i, j), 2.0f);
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
                delta -= Math.pow(motif.get(i, l), 2.0f);
                delta += Math.pow(motif.get(j, l), 2.0f);
            }
        }

        for(Node w : Sets.difference(index.neighborsOf(v), index.neighborsOf(u))) {
            if(w != u) {
                int l = w.getPosition();
                delta -= Math.pow(motif.get(j, l), 2.0f);
                delta += Math.pow(motif.get(i, l), 2.0f);
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
        ConsensusMatrix cm = ConsensusMatrix.read(new File(args[0]));
        Network network = new Network();
        NetworkReader.read(network, new File(args[1]));

        AlignMatrix aligner = new AlignMatrix(cm, network);
        aligner.run(Integer.parseInt(args[2]));
    }
}
