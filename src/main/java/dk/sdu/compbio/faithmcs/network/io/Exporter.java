package dk.sdu.compbio.faithmcs.network.io;

import dk.sdu.compbio.faithmcs.network.Network;

import java.io.File;
import java.io.FileNotFoundException;

interface Exporter {
    void write(Network network, File file) throws FileNotFoundException;
}
