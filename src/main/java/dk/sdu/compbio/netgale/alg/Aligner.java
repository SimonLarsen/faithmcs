package dk.sdu.compbio.netgale.alg;

import dk.sdu.compbio.netgale.Alignment;

public interface Aligner {
    void step();
    void run(int iterations);
    Alignment getAlignment();
}
