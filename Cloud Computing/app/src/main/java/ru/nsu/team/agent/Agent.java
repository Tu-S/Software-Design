package ru.nsu.team.agent;

import ru.nsu.team.tools.KeyValuePair;

import java.lang.instrument.Instrumentation;
import java.util.LinkedList;

public class Agent {

    public static LinkedList<KeyValuePair<String, byte[]>> loadedClasses = new LinkedList<KeyValuePair<String, byte[]>>();

    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new ClassCollector(loadedClasses));
        for (var cl : inst.getAllLoadedClasses()) {
            System.out.println(cl.getName());
        }
    }

    public static void main(String[] args) {
        System.out.println("Heh");
    }

}
