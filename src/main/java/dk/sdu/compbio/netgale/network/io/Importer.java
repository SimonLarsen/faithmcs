package dk.sdu.compbio.netgale.network.io;

import dk.sdu.compbio.netgale.network.Network;

import java.io.File;
import java.io.FileNotFoundException;

interface Importer {
    void read(Network network, File file) throws FileNotFoundException, ImportException;
}
