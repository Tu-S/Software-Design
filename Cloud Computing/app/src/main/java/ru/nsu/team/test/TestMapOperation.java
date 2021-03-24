package ru.nsu.team.test;

import ru.nsu.team.annotation.Remote;
import ru.nsu.team.client.CloudMapOperation;

import java.io.Serializable;

public class TestMapOperation {

    @Remote
    public static Person[] staticMethod(Person[] input) {
        for (Person p : input) {
            p.age = p.age * 2;
            p.name = p.name.replaceAll("name", "hehsdf");
        }
        return input;
//        var ans = new Integer[input.length];
//        for (int i = 0; i < input.length; i++) {
//            ans[i] = Integer.parseInt(input[i])*3;
//        }
//        return ans;
    }

//    @Remote
//    public Person[] execute(Person[] input) {
//        for (Person p : input) {
//            p.age = p.age * 2;
//            p.name = p.name.replaceAll("name", "hehh");
//        }
//        return input;
////        var ans = new Integer[input.length];
////        for (int i = 0; i < input.length; i++) {
////            ans[i] = Integer.parseInt(input[i])*3;
////        }
////        return ans;
//    }
}
