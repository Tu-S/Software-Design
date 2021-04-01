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

    public int command;

    public CloudPacket(LinkedList<KeyValuePair<String, byte[]>> classCodes, byte[] operationClass,
                       byte[] dataClass, byte[] data,
                       boolean isMapOperation, int hashCode, int command) {
        this.classCodes = classCodes;
        this.operationClass = operationClass;
        this.dataClass = dataClass;
        this.data = data;
        this.isMapOperation = isMapOperation;
        this.hashCode = hashCode;
        this.command = command;
    }

    public CloudPacket(byte[] operationClass,
                       byte[] dataClass,
                       int hashCode, int command) {
        this.operationClass = operationClass;
        this.dataClass = dataClass;
        this.hashCode = hashCode;
        this.command = command;
    }
}
