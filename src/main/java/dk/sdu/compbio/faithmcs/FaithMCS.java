package dk.sdu.compbio.faithmcs;

import dk.sdu.compbio.faithmcs.alg.DirectedIteratedLocalSearch;
import dk.sdu.compbio.faithmcs.alg.IteratedLocalSearch;
import dk.sdu.compbio.faithmcs.alg.UndirectedIteratedLocalSearch;
import dk.sdu.compbio.faithmcs.network.DirectedNetwork;
import dk.sdu.compbio.faithmcs.network.UndirectedNetwork;
import dk.sdu.compbio.faithmcs.network.Node;
import dk.sdu.compbio.faithmcs.network.io.ImportException;
import dk.sdu.compbio.faithmcs.network.io.NetworkReader;
import dk.sdu.compbio.faithmcs.network.io.NetworkWriter;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FaithMCS {
    private static final int DEFAULT_EXCEPTIONS = 0;
    private static final float DEFAULT_PERTURBATION = 0.2f;
    private static final int DEFAULT_MAX_NONIMPROVING = 20;

    public static void main(String[] args) throws ParseException, FileNotFoundException, ImportException {
        Options options = new Options();
        options.addOption("h", "help", false, "Show this help text");
        options.addOption("d", "directed", false, "Treat networks as directed.");
        options.addOption("i", "max-nonimproving", true, String.format("Stop algorithm after this number of non-improving iterations. Default: %d.", DEFAULT_MAX_NONIMPROVING));
        options.addOption("p", "perturbation", true, String.format("Ratio of node to swap during perturbation. Default: %f.", DEFAULT_PERTURBATION));
        options.addOption("e", "exceptions", true, String.format("Number of exceptions allowed per edge in solution. Default: %d.", DEFAULT_EXCEPTIONS));
        options.addOption(null, "remove-exception-leaves", false, "Remove leaf connected by an exception edge from solution.");
        options.addOption("o", "output", true, "Output alignment table to file.");
        options.addOption("n", "network", true, "Output conserved subgraph to file.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        HelpFormatter help_formatter = new HelpFormatter();

        if (cmd.getArgList().size() < 2) {
            System.err.println("error: Needs at least two networks for alignment.");
            help_formatter.printHelp("FaithMCS [OPTIONS] network1 network2 [network3 ...]", options);
            System.exit(1);
        }

        if (cmd.hasOption("help")) {
            help_formatter.printHelp("FaithMCS [OPTIONS] network1 network2 [network3 ...]", options);
            System.exit(1);
        }

        int max_nonimproving = Integer.parseInt(cmd.getOptionValue("max-nonimproving", Integer.toString(DEFAULT_MAX_NONIMPROVING)));
        float perturbation = Float.parseFloat(cmd.getOptionValue("perturbation", Float.toString(DEFAULT_PERTURBATION)));

        IteratedLocalSearch aligner;

        boolean directed = cmd.hasOption("directed");
        if(directed) {
            System.err.println("Treating networks as undirected");
            List<DirectedNetwork> networks = new ArrayList<>();
            for(String path : cmd.getArgList()) {
                DirectedNetwork network = new DirectedNetwork();
                NetworkReader.read(network, new File(path));
                networks.add(network);
            }

            aligner = new DirectedIteratedLocalSearch(networks, perturbation);
        }
        // undirected
        else {
            System.err.println("Treating networks as directed");
            List<UndirectedNetwork> networks = new ArrayList<>();
            for(String path : cmd.getArgList()) {
                UndirectedNetwork network = new UndirectedNetwork();
                NetworkReader.read(network, new File(path));
                networks.add(network);
            }

            aligner = new UndirectedIteratedLocalSearch(networks, perturbation);
        }

        aligner.run(max_nonimproving);
        Alignment alignment = aligner.getAlignment();

        if (cmd.hasOption("output")) {
            writeAlignment(alignment, new File(cmd.getOptionValue("output")));
        }

        if (cmd.hasOption("network")) {
            int exceptions = Integer.parseInt(cmd.getOptionValue("exceptions", Integer.toString(DEFAULT_EXCEPTIONS)));
            boolean remove_exception_leaves = cmd.hasOption("remove-exception-leaves");
            NetworkWriter.write(alignment.buildNetwork(exceptions, remove_exception_leaves), new File(cmd.getOptionValue("network")));
        }
    }

    private static void writeAlignment(Alignment alignment, File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        List<List<Node>> align = alignment.getAlignment();
        int n = align.size();
        int M = align.get(0).size();

        for (int j = 0; j < M; ++j) {
            int finalJ = j;
            pw.println(align.stream().map(nodes -> nodes.get(finalJ))
                    .filter(node -> !node.isFake())
                    .map(Node::toString)
                    .collect(Collectors.joining("\t"))
            );
        }
        pw.close();
    }
}
