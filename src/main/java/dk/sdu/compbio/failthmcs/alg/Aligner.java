package dk.sdu.compbio.failthmcs.alg;

public interface Aligner {
    void step();
    void run(int iterations);
    dk.sdu.compbio.failthmcs.Alignment getAlignment();
    int getCurrentNumberOfEdges();
    int getBestNumberOfEdges();
}
