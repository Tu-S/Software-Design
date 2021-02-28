package ru.nsu.team.packages;

import ru.nsu.team.tools.KeyValuePair;

import java.io.Serializable;
import java.util.LinkedList;

public class PackageToServer implements Serializable {

    public PackageToServer() {
    }

    public LinkedList<KeyValuePair<String, byte[]>> classCodes;

    public byte[] operationClass;

    public byte[] dataClass;

    public byte[] data;

    public boolean isMapOperation;

    public PackageToServer(LinkedList<KeyValuePair<String, byte[]>> classCodes, byte[] operationClass, byte[] dataClass, byte[] data, boolean isMapOperation) {
        this.classCodes = classCodes;
        this.operationClass = operationClass;
        this.dataClass = dataClass;
        this.data = data;
        this.isMapOperation = isMapOperation;
    }
}
