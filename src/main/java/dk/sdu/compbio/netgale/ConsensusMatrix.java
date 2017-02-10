package dk.sdu.compbio.netgale;

public class ConsensusMatrix {
    private final int M;
    private final float[] values;

    public ConsensusMatrix(int M) {
        this.M = M;
        this.values = new float[M*(M+1)/2];
    }

    public ConsensusMatrix(EdgeMatrix em, int n) {
        this.M = em.getSize();
        this.values = new float[M*(M+1)/2];
        for(int i = 0; i < M; ++i) {
            for(int j = 0; j < M; ++j) { values[index(i, j)] = em.get(i, j) / (float)n;
            }
        }
    }

    public float get(int i, int j) {
        return values[index(i, j)];
    }

    public void set(int i, int j, int value) {
        values[index(i, j)] = value;
    }

    private int index(int i, int j) {
        if(i <= j) return i * M + j - (i * (i+1) / 2);
        else return j * M + i - (j * (j+1) / 2);
    }
}
