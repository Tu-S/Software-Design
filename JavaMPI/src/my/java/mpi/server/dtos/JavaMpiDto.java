package my.java.mpi.server.dtos;

import my.java.mpi.common.KeyValuePair;

import java.io.Serializable;
import java.util.LinkedList;

public class JavaMpiDto implements Serializable {

    public JavaMpiDto() {
    }

    public LinkedList<KeyValuePair<String, byte[]>> classCodes;

    public byte[] operationClass;

    public byte[] dataClass;

    public byte[] data;

    public boolean isMapOperation;

    public JavaMpiDto(LinkedList<KeyValuePair<String, byte[]>> classCodes, byte[] operationClass, byte[] dataClass, byte[] data, boolean isMapOperation) {
        this.classCodes = classCodes;
        this.operationClass = operationClass;
        this.dataClass = dataClass;
        this.data = data;
        this.isMapOperation = isMapOperation;
    }
}
