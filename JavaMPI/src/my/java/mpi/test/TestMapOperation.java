package my.java.mpi.test;

import my.java.mpi.annoatation.Remote;
import my.java.mpi.client.AJavaMpiOperation;

public class TestMapOperation /*extends AJavaMpiOperation<Integer[], Integer[]>*/ {
    //@Override
    @Remote
    public Integer[] Execute(Integer[] input) {
        var ans = new Integer[input.length];
        for (int i = 0; i < input.length; i++) {
            ans[i] = i*2;
        }
        return ans;
    }
}
