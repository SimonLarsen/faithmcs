package dk.sdu.compbio.netgale.network.io;

import dk.sdu.compbio.netgale.network.Network;

import java.io.File;
import java.io.FileNotFoundException;

interface Exporter {
    void write(Network network, File file) throws FileNotFoundException;
}
