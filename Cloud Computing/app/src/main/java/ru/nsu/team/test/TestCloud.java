package ru.nsu.team.test;

import ru.nsu.team.client.CloudExecutor;

import java.io.IOException;

public class TestCloud {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        CloudExecutor.init("localhost", 18228);
        var source = new Person[10];
        for (int i = 0; i < source.length; i++) {
            source[i] = new Person(i, "person name " + i);
        }

        //List<Person> personsStream = (ArrayList<Person>)CloudExecutor.testExecute(source,TestMapOperation.class).collect(Collectors.toCollection(ArrayList::new));

        System.out.println("Static method:");
        Person[] persons = (Person[]) CloudExecutor.execute(source, TestMapOperation::staticMethod);

        for (Person p : persons) {
            System.out.println(" " + p.name);
        }

        System.out.println("Non-static method:");
        persons = (Person[]) CloudExecutor.execute(source, new TestMapOperation()::execute);

        for (Person p : persons) {
            System.out.println(" " + p.name);
        }
        var integers = new Integer[10];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = i;
        }

        System.out.println("Lambda:");
        Integer[] res = (Integer[]) CloudExecutor.execute(integers,n->n);

        for (Integer p : res) {
            System.out.println(p);
        }
    }
}
