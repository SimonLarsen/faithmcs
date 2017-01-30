package dk.sdu.compbio.netgale;

import dk.sdu.compbio.netgale.network.Network;
import dk.sdu.compbio.netgale.network.NetworkReader;
import dk.sdu.compbio.netgale.network.Node;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class JNetGALE {
    public static void main(String[] args) throws ParseException, FileNotFoundException {
        Option output_option = Option.builder("o").longOpt("output").hasArg().build();

        Options options = new Options();
        options.addOption(output_option);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        NetworkReader reader = new NetworkReader();
        List<Network> networks = cmd.getArgList().stream().map(File::new).map(reader::read).collect(Collectors.toList());

        Model model = new Model(0);
        Aligner aligner = new SimulatedAnnealingAligner(1.0f, 100000);
        Alignment alignment = aligner.align(networks, model);

        if(cmd.hasOption("o")) {
            writeAlignment(alignment, new File(cmd.getOptionValue("o")));
        }
    }

    private static void writeAlignment(Alignment alignment, File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        for(List<Node> nodes : alignment.getAlignment()) {
            pw.println(nodes.stream()
                    .map(Node::toString)
                    .collect(Collectors.joining("\t"))
            );
        }
        pw.close();
    }
}
