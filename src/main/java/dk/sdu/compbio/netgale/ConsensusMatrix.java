package dk.sdu.compbio.netgale;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConsensusMatrix {
    private final int M;
    private final float[] values;

    public ConsensusMatrix(int M) {
        this.M = M;
        this.values = new float[M*(M+1)/2];
    }

    public ConsensusMatrix(EdgeMatrix em, int n) {
        this.M = em.size();
        this.values = new float[M*(M+1)/2];
        for(int i = 0; i < M-1; ++i) {
            for(int j = 0; j < M; ++j) {
                values[index(i, j)] = em.get(i, j) / (float)n;
            }
        }
    }

    public int size() {
        return M;
    }

    public float get(int i, int j) {
        return values[index(i, j)];
    }

    public void set(int i, int j, float value) {
        values[index(i, j)] = value;
    }

    private int index(int i, int j) {
        if(i <= j) return i * M + j - (i * (i+1) / 2);
        else return j * M + i - (j * (j+1) / 2);
    }

    public void write(File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        pw.println(M);
        pw.println(
                IntStream.range(0, values.length)
                        .mapToObj(i -> Float.toString(values[i]))
                        .collect(Collectors.joining("\n"))
        );
        pw.close();
    }

    public static ConsensusMatrix read(File file) throws FileNotFoundException {
        Scanner scan = new Scanner(file);

        int M = Integer.parseInt(scan.nextLine());
        ConsensusMatrix cm = new ConsensusMatrix(M);
        for(int i = 0; i < M-1; ++i) {
            for(int j = i+1; j < M; ++j) {
                cm.set(i, j, Float.parseFloat(scan.nextLine()));
            }
        }

        while(scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            if(line.length() == 0) continue;

        }
        scan.close();

        return cm;
    }
}
