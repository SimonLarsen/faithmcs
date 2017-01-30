package dk.sdu.compbio.netgale;

import dk.sdu.compbio.netgale.network.Network;

import java.util.List;

public class SimulatedAnnealingAligner implements Aligner {
    private final float start_temperature;
    private final int max_iterations;

    public SimulatedAnnealingAligner(float start_temperature, int max_iterations) {
        this.start_temperature = start_temperature;
        this.max_iterations = max_iterations;
    }

    @Override
    public Alignment align(List<Network> networks, Model model) {
        for(int iteration = 0; iteration < max_iterations; ++iteration) {
            float temperature = (1.0f - iteration / max_iterations) * start_temperature;
        }
        return null;
    }
}
