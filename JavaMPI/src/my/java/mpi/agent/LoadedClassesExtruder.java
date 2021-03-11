package my.java.mpi.agent;

import my.java.mpi.common.KeyValuePair;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.LinkedList;

public class LoadedClassesExtruder implements ClassFileTransformer {

    private LinkedList<KeyValuePair<String,byte[]>> classesCodes;

    public LoadedClassesExtruder(LinkedList<KeyValuePair<String,byte[]>> classesCodes) {
        this.classesCodes = classesCodes;
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        classesCodes.add(new KeyValuePair<String, byte[]>(classBeingRedefined.getCanonicalName(), classfileBuffer.clone()));
        System.out.println("In inst ============================================ " + className);
        return classfileBuffer;
    }
}
