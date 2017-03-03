package dk.sdu.compbio.failthmcs.network.io;

import java.io.File;
import java.io.FileNotFoundException;

public class NetworkWriter {
    public static void write(dk.sdu.compbio.failthmcs.network.Network network, File file) throws FileNotFoundException {
        String path = file.getPath();
        int dotpos = path.lastIndexOf('.');
        if(dotpos == -1 || dotpos == path.length()) {
            throw new IllegalArgumentException("Cannot determine file type from filename");
        }
        String ending = path.substring(dotpos+1);

        Exporter exporter = null;

        switch(ending) {
            case "sif":
                exporter = new dk.sdu.compbio.failthmcs.network.io.SIFExporter(); break;
            case "gw":
            case "leda":
                exporter = new dk.sdu.compbio.failthmcs.network.io.LEDAExporter(); break;
        }

        if(exporter == null) throw new IllegalArgumentException("Unrecognized file format: " + ending);

        exporter.write(network, file);
    }
}
