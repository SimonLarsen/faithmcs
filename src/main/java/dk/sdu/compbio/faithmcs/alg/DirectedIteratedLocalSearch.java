package dk.sdu.compbio.faithmcs.alg;

import dk.sdu.compbio.faithmcs.Alignment;

public class DirectedIteratedLocalSearch implements IteratedLocalSearch {
    @Override
    public boolean step() {
        return false;
    }

    @Override
    public void run(int iterations) {

    }

    @Override
    public Alignment getAlignment() {
        return null;
    }

    @Override
    public int getCurrentNumberOfEdges() {
        return 0;
    }

    @Override
    public int getBestNumberOfEdges() {
        return 0;
    }

    @Override
    public void setPerturbationAmount(float a) {

    }
}
