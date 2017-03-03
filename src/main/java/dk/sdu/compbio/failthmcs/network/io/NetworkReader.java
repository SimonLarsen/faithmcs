package dk.sdu.compbio.failthmcs.network.io;

import java.io.File;
import java.io.FileNotFoundException;

public class NetworkReader {
    public static void read(dk.sdu.compbio.failthmcs.network.Network network, File file) throws FileNotFoundException, ImportException {
        String path = file.getPath();
        int dotpos = path.lastIndexOf('.');
        if(dotpos == -1 || dotpos == path.length()) {
            throw new IllegalArgumentException("Cannot determine file type from filename.");
        }
        String ending = path.substring(dotpos+1);

        dk.sdu.compbio.failthmcs.network.io.Importer importer = null;
        switch(ending) {
            case "sif":
                importer = new dk.sdu.compbio.failthmcs.network.io.SIFImporter(); break;
            case "gw":
            case "leda":
                importer = new dk.sdu.compbio.failthmcs.network.io.LEDAImporter(); break;
        }

        if(importer == null) throw new IllegalArgumentException("Unrecognized file format: " + ending);

        importer.read(network, file);
    }
}
