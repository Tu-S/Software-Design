package ru.nsu.team.tools;

import java.io.Serializable;

public class KeyValuePair <K extends Serializable, V extends Serializable> implements Serializable {
    public K key;
    public V value;

    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
