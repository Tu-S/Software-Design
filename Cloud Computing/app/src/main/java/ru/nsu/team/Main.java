package ru.nsu.team;

import ru.nsu.team.client.CloudExecutor;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {


        var source = new Person[10];
        var personsExpected = new Person[10];
        for (int i = 0; i < source.length; i++) {
            source[i] = new Person(i, "person name " + i);
            personsExpected[i] = new Person(i * 2, "person static method " + i);
        }
        List<Person> list = Arrays.asList(source);

        CloudExecutor.Execute(list,TestMapOperation::staticMethod,5);
        //ArrayList<Object> res = CloudExecutor.execute(source, TestMapOperation::staticMethod).collect(Collectors.toCollection(ArrayList<Object>::new));



    }

}
