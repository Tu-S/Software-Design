package ru.nsu.team;

import ru.nsu.team.client.CloudExecutor;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CloudExecutor.init("localhost", 18228);

        var source = new Person[10];

        var personsExpected = new Person[10];
        for (int i = 0; i < source.length; i++) {
            source[i] = new Person(i, "person name " + i);
            personsExpected[i] = new Person(i * 2, "person static method " + i);
        }
        List<Person> list = Arrays.asList(source);
        CloudExecutor<Person, Object[]> cloud = new CloudExecutor<>();

        cloud.loadData(list,5);
        cloud.applyFunction(TestMapOperation::testStaticMethod);
        cloud.applyFunction(TestMapOperation::testStaticMethod);
        cloud.applyFunction(TestMapOperation::test2StaticMethod);
        var res = cloud.collect();
        Arrays.stream(res).forEach(p -> System.out.println(((Person) p).age));
    }

}
