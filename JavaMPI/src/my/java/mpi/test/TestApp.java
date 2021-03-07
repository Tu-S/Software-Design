package my.java.mpi.test;

import my.java.mpi.client.JavaMpi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class TestApp {
    public static void main(String[] args) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        JavaMpi.Init("localhost", 18228);
        var source = new Integer[10];
        for (int i = 0; i < source.length; i++) {
            source[i] = i;
        }
        JavaMpi.testExecute(source,TestMapOperation.class).forEach(System.out::println);
        //var result = JavaMpi.Execute(source, Integer[].class, TestMapOperation.class, String[].class);
//        for (var el : result) {
//            System.out.println(el);
//        }

//        var result2 = JavaMpi.testExecute(source,TestReduceOperation.class);
//        for(Object o : result2){
//            System.out.println(o);
//        }


//        var task = JavaMpi.Execute(Arrays.asList(source), TrueTestMapOperation.class, 10);
//        var exec = Executors.newSingleThreadExecutor();
//        var result3 = exec.submit(task).get();
//        for (var el : result3) {
//            System.out.println(el);
//        }
//        exec.shutdown();
    }
}
