package dk.sdu.compbio.netgale;

import dk.sdu.compbio.netgale.network.Network;
import dk.sdu.compbio.netgale.network.NetworkReader;
import dk.sdu.compbio.netgale.network.Node;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
        List<Network> networks = new ArrayList<>();
        for(String path : cmd.getArgList()) {
            Network network = new Network();
            reader.read(network, new File(path));
            networks.add(network);
        }

        Model model = new Model(2.0f);
        Aligner aligner = new SimulatedAnnealingAligner(model, 1.0f,100000);
        Alignment alignment = aligner.align(networks, model);

        for(List<Node> nodes : alignment.getAlignment()) {
            System.out.println(nodes.stream().map(Node::toString).collect(Collectors.joining("\t")));
        }

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
