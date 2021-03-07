package my.java.mpi.test;

import my.java.mpi.annoatation.Remote;
import my.java.mpi.client.AJavaMpiOperation;

import java.util.Arrays;

public class TestReduceOperation extends AJavaMpiOperation<Integer[], String[]> {
    @Override
    @Remote
    public String[] Execute(Integer[] input) {
        var str = new StringBuilder();
        for (var item : input) {
            str.append(item);
        }
        return str.toString().split("");
    }
}
