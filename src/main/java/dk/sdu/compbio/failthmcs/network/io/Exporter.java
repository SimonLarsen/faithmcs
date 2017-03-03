package dk.sdu.compbio.failthmcs.network.io;

import java.io.File;
import java.io.FileNotFoundException;

interface Exporter {
    void write(dk.sdu.compbio.failthmcs.network.Network network, File file) throws FileNotFoundException;
}
