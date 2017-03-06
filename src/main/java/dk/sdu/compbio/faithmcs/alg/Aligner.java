package dk.sdu.compbio.faithmcs.alg;

import dk.sdu.compbio.faithmcs.Alignment;

public interface Aligner {
    void step();
    void run(int iterations);
    Alignment getAlignment();
    int getCurrentNumberOfEdges();
    int getBestNumberOfEdges();
}
