package ru.nsu.team.test;

import ru.nsu.team.client.CloudExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestCloud {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        CloudExecutor.init("localhost", 18228);
        var source = new Person[10];
        for (int i = 0; i < source.length; i++) {
            source[i] = new Person(i, "person name " + i);
        }
        List<Person> personsStream = (ArrayList<Person>)CloudExecutor.testExecute(source,TestMapOperation.class).collect(Collectors.toCollection(ArrayList::new));
        Person[] persons = (Person[]) CloudExecutor.staticExecute(source, TestMapOperation::staticMethod);

        for (Person p : persons) {
            System.out.println(p.name);
        }
    }

}
