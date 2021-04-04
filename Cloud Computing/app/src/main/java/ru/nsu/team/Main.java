package ru.nsu.team;

import ru.nsu.team.client.CloudExecutor;
import ru.nsu.team.node.Node;
import ru.nsu.team.server.Server;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
//        CloudExecutor.init("localhost", 18228);
//        var server = new Thread(() -> {
//            try {
//                Server.main(null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        server.start();
//        var node1 = new Thread(() -> {
//            try {
//                Node.main(null);
//            } catch (IOException | ClassNotFoundException | ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        node1.start();
//        var node2 = new Thread(() -> {
//            try {
//                Node.main(null);
//            } catch (IOException | ClassNotFoundException | ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        node2.start();
        CloudExecutor.init("localhost", 18228);

        var source = new Person[10];

        var personsExpected = new Person[10];
        for (int i = 0; i < source.length; i++) {
            source[i] = new Person(i, "person name " + i);
            personsExpected[i] = new Person(i * 2, "person static method " + i);
        }
        List<Person> list = Arrays.asList(source);
        CloudExecutor<Person, Object[]> cloud = new CloudExecutor();

        cloud.testCloudMap(list, TestMapOperation::testStaticMethod, 5);
        //cloud.applyFunction(TestMapOperation::testStaticMethod);
        Arrays.stream(cloud.collect()).forEach(p -> System.out.println(((Person) p).age));
//        var res = CloudExecutor.collect();
//        for (var e : res) {
//            System.out.println(((Person) e).age);
//
//        }
        //ArrayList<Object> res = CloudExecutor.execute(source, TestMapOperation::staticMethod).collect(Collectors.toCollection(ArrayList<Object>::new));


    }

}
