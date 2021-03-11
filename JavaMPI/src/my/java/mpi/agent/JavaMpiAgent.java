package my.java.mpi.agent;

import my.java.mpi.common.KeyValuePair;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.LinkedList;

public class JavaMpiAgent {

  public static LinkedList<KeyValuePair<String, byte[]>> allLoadedClasses =
      new LinkedList<KeyValuePair<String, byte[]>>();
  public static HashMap<String, byte[]> map = new HashMap<>();

  public static void premain(String args, Instrumentation inst) {
    inst.addTransformer(new LoadedClassesExtruder(allLoadedClasses));
    for (var cl : inst.getAllLoadedClasses()) {
      System.out.println(cl.getName());
    }
  }

  public static void main(String[] args) {
    System.out.println("I'm doing nothing, just to create JAR");
  }
}
