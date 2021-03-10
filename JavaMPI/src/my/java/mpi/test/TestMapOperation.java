package my.java.mpi.test;

import my.java.mpi.annoatation.Remote;
import my.java.mpi.client.AJavaMpiOperation;

public class TestMapOperation /*extends AJavaMpiOperation<Integer[], Integer[]>*/ {
    //@Override

    @Remote
    public static Person[] staticMethod(Person[] input) {
        for(Person p : input){
            p.age = p.age*2;
            p.name = p.name.replaceAll("name","heh");
        }
        return input;
//        var ans = new Integer[input.length];
//        for (int i = 0; i < input.length; i++) {
//            ans[i] = Integer.parseInt(input[i])*3;
//        }
//        return ans;
    }
    @Remote
    public Person[] Execute(Person[] input) {
        for(Person p : input){
            p.age = p.age*2;
            p.name = p.name.replaceAll("name","heh");
        }
        return input;
//        var ans = new Integer[input.length];
//        for (int i = 0; i < input.length; i++) {
//            ans[i] = Integer.parseInt(input[i])*3;
//        }
//        return ans;
    }
}
