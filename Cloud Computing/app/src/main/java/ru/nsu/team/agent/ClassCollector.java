package ru.nsu.team.agent;

import ru.nsu.team.tools.KeyValuePair;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.LinkedList;

public class ClassCollector implements ClassFileTransformer {
    private final LinkedList<KeyValuePair<String, byte[]>> classesCodes;

    public ClassCollector(LinkedList<KeyValuePair<String, byte[]>> classesCodes) {
        this.classesCodes = classesCodes;
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        classesCodes.add(new KeyValuePair<>(classBeingRedefined.getCanonicalName(), classfileBuffer.clone()));
        return classfileBuffer;
    }
}
