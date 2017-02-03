package dk.sdu.compbio.netgale.network.io;

import dk.sdu.compbio.netgale.network.Network;

import java.io.File;
import java.io.FileNotFoundException;

public class NetworkWriter {
    public static void write(Network network, File file) throws FileNotFoundException {
        String path = file.getPath();
        int dotpos = path.lastIndexOf('.');
        if(dotpos == -1 || dotpos == path.length()) {
            throw new IllegalArgumentException("Cannot determine file type from filename");
        }
        String ending = path.substring(dotpos+1);

        Exporter exporter = null;

        switch(ending) {
            case "sif":
                exporter = new SIFExporter(); break;
            case "gw":
            case "leda":
                exporter = new LEDAExporter(); break;
        }

        if(exporter == null) throw new IllegalArgumentException("Unrecognized file format: " + ending);

        exporter.write(network, file);
    }
}
