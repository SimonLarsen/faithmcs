package dk.sdu.compbio.netgale.alg;

import dk.sdu.compbio.netgale.Alignment;
import dk.sdu.compbio.netgale.Model;
import dk.sdu.compbio.netgale.network.Network;

import java.util.List;

public interface Aligner {
    Alignment align(List<Network> networks, Model model);
}
