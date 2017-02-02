package dk.sdu.compbio.netgale.network;

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

        if(ending.equals("sif")) {
            SIFExporter.write(network, file);
        }
        else {
            throw new IllegalArgumentException("Unrecognized file format: " + ending);
        }
    }
}
