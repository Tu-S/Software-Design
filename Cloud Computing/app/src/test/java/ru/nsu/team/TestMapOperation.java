package ru.nsu.team;

public class TestMapOperation {

    public static Person[] staticMethod(Person[] input) {
        for (Person p : input) {
            p.age = p.age * 2;
            p.name = p.name.replaceAll("name", "static method");
        }
        return input;
    }

    public Person[] execute(Person[] input) {
        for (Person p : input) {
            p.age = p.age * 2;
            p.name = p.name.replaceAll("name", "non-static method");
        }
        return input;
    }
}
