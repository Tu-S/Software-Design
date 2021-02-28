package ru.nsu.team.node;

import ru.nsu.team.tools.KeyValuePair;

import java.util.HashMap;
import java.util.LinkedList;

public class ClassInjector extends ClassLoader {

    public ClassInjector(ClassLoader parent) {
        super(parent);
    }

    private HashMap<String, byte[]> classesCodes = new HashMap<String, byte[]>();

    public void InjectClasses(LinkedList<KeyValuePair<String, byte[]>> classesToLoad) {

        for (var cl : classesToLoad) {
            try {
                classesCodes.put(cl.key, cl.value);
                loadClass(cl.key);
                System.out.println("Injected class: " + cl.key);
            } catch (ClassNotFoundException e) {
                System.err.println("Failed to inject class: " + cl.key);
                System.err.println("\tReason: " + e.getMessage());
            }
        }
    }

    @Override
    public Class findClass(String name) {
        byte[] data = classesCodes.get(name);
        return defineClass(name, data, 0, data.length);
    }
}
