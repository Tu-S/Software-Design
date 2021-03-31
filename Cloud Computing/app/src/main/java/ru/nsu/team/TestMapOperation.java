package ru.nsu.team;

import java.util.Collection;
import java.util.List;

public class TestMapOperation {

    public static Person[] staticMethod(Person[] input) {
        for (Person p : input) {
            p.age = p.age * 2;
            p.name = p.name.replaceAll("name", "static method");
        }
        return input;
    }

    public static Object[] testStaticMethod(Object[] input) {
        for (Object t : input) {
            var p = (Person)t;
            p.age = p.age * 2;
            p.name = p.name.replaceAll("name", "static method");
            System.out.println(p.age);
        }
        return  input;
    }

    public Person[] nonStaticMethod(Person[] input) {
        for (Person p : input) {
            p.age = p.age * 2;
            p.name = p.name.replaceAll("name", "non-static method");
        }
        return input;
    }

    public static Integer[] getAge(Person[] persons) {
        Integer[] res = new Integer[persons.length];
        int i = 0;
        for (Person p : persons) {
            res[i] = p.age * 2;
            i++;
        }
        return res;

    }
}
