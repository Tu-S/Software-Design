package ru.nsu.team.test;

import ru.nsu.team.client.CloudExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestCloud {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        /*CloudExecutor.init("localhost", 18228);
        var source = new Integer[10];
        for (int i = 0; i < source.length; i++) {
            source[i] = i;
        }*/
        List<Integer> list = new ArrayList<>();
        for(int i = 0 ; i < 10;i++){
            list.add(i);
        }
        //list.stream().map(MyFunc::method).forEach(System.out::println);
        MyFunc f = new MyFunc();
        var res = CloudExecutor.cloudMap(list,MyFunc::staticMethod);
        for(Integer i : res.collect(Collectors.toCollection(ArrayList::new))){
            System.out.println(i);
        }
        /*var result = CloudExecutor.execute(source, Integer[].class, TestOperation.class, String[].class);
        for (var el : result) {
            System.out.println(el);
        }*/


    }

}
