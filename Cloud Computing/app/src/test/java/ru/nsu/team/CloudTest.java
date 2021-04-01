//package ru.nsu.team;
//
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import ru.nsu.team.client.CloudExecutor;
//import ru.nsu.team.node.Node;
//import ru.nsu.team.server.Server;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.concurrent.ExecutionException;
//import java.util.stream.Collectors;
//
//public class CloudTest {
//    private static Thread server;
//    private static Thread node;
//
//    @BeforeClass
//    public static void init() {
//        server = new Thread(() -> {
//            try {
//                Server.main(null);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        server.start();
//        node = new Thread(() -> {
//            try {
//                Node.main(null);
//            } catch (IOException | ClassNotFoundException | ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        node.start();
//        CloudExecutor.init("localhost", 18228);
//    }
//
//    @AfterClass
//    public static void finish() {
//        server.stop();
//        node.stop();
//    }
//
//    @Test
//    public void staticMethodTest() throws IOException, ClassNotFoundException {
//        var source = new Person[10];
//        var personsExpected = new Person[10];
//        for (int i = 0; i < source.length; i++) {
//            source[i] = new Person(i, "person name " + i);
//            personsExpected[i] = new Person(i * 2, "person static method " + i);
//        }
//
//        ArrayList<Object> res = CloudExecutor.execute(source, TestMapOperation::staticMethod).collect(Collectors.toCollection(ArrayList<Object>::new));
//        var actualPersons = res.toArray(Person[]::new);
//        Assert.assertArrayEquals(personsExpected, actualPersons);
//    }
//
//    @Test
//    public void nonStaticMethodTest() throws IOException, ClassNotFoundException {
//        var source = new Person[10];
//        var personsExpected = new Person[10];
//        for (int i = 0; i < source.length; i++) {
//            source[i] = new Person(i, "person name " + i);
//            personsExpected[i] = new Person(i * 2, "person non-static method " + i);
//        }
//        var res = CloudExecutor.execute(source, new TestMapOperation()::nonStaticMethod).collect(Collectors.toCollection(ArrayList<Object>::new));
//        var actualPersons = res.toArray(Person[]::new);
//        Assert.assertArrayEquals(personsExpected, actualPersons);
//    }
//
//    @Test
//    public void getAgeStaticMethodTest() throws IOException, ClassNotFoundException {
//        var source = new Person[10];
//        var resExpected = new Integer[10];
//        for (int i = 0; i < source.length; i++) {
//            source[i] = new Person(i, "person name " + i);
//            resExpected[i] = i * 2;
//        }
//        var res = CloudExecutor.execute(source, TestMapOperation::getAge).collect(Collectors.toCollection(ArrayList<Object>::new));
//        var actualRes = res.toArray(Integer[]::new);
//        Assert.assertArrayEquals(resExpected, actualRes);
//    }
//
//    @Test
//    public void lambdaClosureTest() throws IOException, ClassNotFoundException {
//        var integers = new Integer[10];
//        var resultExpected = new Integer[10];
//        final int a = 300;
//
//        for (int i = 0; i < integers.length; i++) {
//            integers[i] = i;
//            resultExpected[i] = i * a;
//        }
//
//        var res = CloudExecutor.execute(integers, n -> {
//            for (int i = 0; i < n.length; i++) {
//                n[i] *= a;
//            }
//            return n;
//        }).collect(Collectors.toCollection(ArrayList<Object>::new));
//        var actualIntegers = res.toArray(Integer[]::new);
//        Assert.assertArrayEquals(resultExpected, actualIntegers);
//    }
//}
