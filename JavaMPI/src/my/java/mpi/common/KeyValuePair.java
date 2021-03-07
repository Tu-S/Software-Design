package my.java.mpi.common;

import java.io.Serializable;

public class KeyValuePair <K extends Serializable, V extends Serializable> implements Serializable {
    public K key;
    public V value;

    public KeyValuePair() {
    }

    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
