package ru.nsu.team.packet;

import ru.nsu.team.tools.KeyValuePair;

import java.io.Serializable;
import java.util.LinkedList;

public class CloudPacket implements Serializable {

    public LinkedList<KeyValuePair<String, byte[]>> classCodes;

    public byte[] operationClass;

    public byte[] dataClass;

    public byte[] data;

    public int hashCode;

    public boolean isMapOperation;

    public CloudPacket(LinkedList<KeyValuePair<String, byte[]>> classCodes, byte[] operationClass,
                       byte[] dataClass, byte[] data,
                       boolean isMapOperation, int hashCode) {
        this.classCodes = classCodes;
        this.operationClass = operationClass;
        this.dataClass = dataClass;
        this.data = data;
        this.isMapOperation = isMapOperation;
        this.hashCode = hashCode;
    }
}
