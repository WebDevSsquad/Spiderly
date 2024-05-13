package Indexer;

public class Pair<K, V> {
    private K first;
    private V second;

    public Pair() {
        first = null;
        second = null;
    }
    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public void setFirst(K first) {
        this.first = first;
    }

    public V getSecond() {
        return second;
    }

    public void setSecond(V second) {
        this.second = second;
    }

    public boolean isEmpty() {
        return first == null && second == null;
    }
}
