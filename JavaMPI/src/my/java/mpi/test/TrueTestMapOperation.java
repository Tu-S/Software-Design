package my.java.mpi.test;

import my.java.mpi.annoatation.Remote;
import my.java.mpi.client.AJavaMpiMapOperation;

public class TrueTestMapOperation extends AJavaMpiMapOperation<Integer, String> {

    @Override
    @Remote
    public String Convert(Integer input) {
        return String.format("%dLOL%d", input, input);
    }
}
