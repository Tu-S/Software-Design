package ru.nsu.team;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.nsu.team.client.CloudExecutor;
import ru.nsu.team.node.Node;
import ru.nsu.team.server.Server;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CloudTest {
  private static Thread server;
  private static Thread node;

  @BeforeClass
  public static void init() {
    server = new Thread(() -> {
      try {
        Server.main(null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    server.start();
    node = new Thread(() -> {
      try {
        Node.main(null);
      } catch (IOException | ClassNotFoundException | ExecutionException | InterruptedException e) {
        e.printStackTrace();
      }
    });
    node.start();
    CloudExecutor.init("localhost", 18228);
  }

  @AfterClass
  public static void finish() {
    server.stop();
    node.stop();
  }

  @Test
  public void staticMethodTest() throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
    var source = new Person[10];
    var personsExpected = new Person[10];
    for (int i = 0; i < source.length; i++) {
      source[i] = new Person(i, "person name " + i);
      personsExpected[i] = new Person(i * 2, "person static method " + i);
    }

    Person[] personsActual = (Person[]) CloudExecutor.execute(source, TestMapOperation::staticMethod);

    Assert.assertArrayEquals(personsExpected, personsActual);
  }

  @Test
  public void nonStaticMethodTest() throws IOException, ClassNotFoundException {
    var source = new Person[10];
    var personsExpected = new Person[10];
    for (int i = 0; i < source.length; i++) {
      source[i] = new Person(i, "person name " + i);
      personsExpected[i] = new Person(i * 2, "person non-static method " + i);
    }

    Person[] personsActual = (Person[]) CloudExecutor.execute(source, new TestMapOperation()::execute);

    Assert.assertArrayEquals(personsExpected, personsActual);
  }

  @Test
  public void lambdaClosureTest() throws IOException, ClassNotFoundException {
    var integers = new Integer[10];
    var resultExpected = new Integer[10];
    final int a = 300;

    for (int i = 0; i < integers.length; i++) {
      integers[i] = i;
      resultExpected[i] = i * a;
    }

    Integer[] resultActual = (Integer[]) CloudExecutor.execute(integers, n -> {
      for (int i = 0; i < n.length; i++) {
        n[i] *= a;
      }
      return n;
    });

    Assert.assertArrayEquals(resultExpected, resultActual);
  }
}
