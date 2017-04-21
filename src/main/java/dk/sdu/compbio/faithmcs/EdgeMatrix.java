package dk.sdu.compbio.faithmcs;

public interface EdgeMatrix {
    int countEdges();
    int size();
    int get(int i, int j);
    void set(int i, int j, int value);
    void increment(int i, int j);
    void decrement(int i, int j);
}
