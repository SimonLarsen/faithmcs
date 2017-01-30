package dk.sdu.compbio.netgale;

import dk.sdu.compbio.netgale.network.Network;

import java.util.List;

public interface Aligner {
    Alignment align(List<Network> networks, Model model);
}
