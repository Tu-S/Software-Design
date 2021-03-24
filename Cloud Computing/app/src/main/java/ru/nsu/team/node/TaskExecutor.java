package ru.nsu.team.node;


import ru.nsu.team.packet.CloudNodeResponsePacket;
import ru.nsu.team.tools.Toolkit;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

public class TaskExecutor<T, TOperation> implements Callable<CloudNodeResponsePacket> {
    private final Class<TOperation> operationClass;
    private final T data;
    private final UUID uuid;
    private final int methodHashCode;

    public TaskExecutor(final Class<TOperation> operationClass, int methodHashCode,
                        final T data, final UUID uuid) {
        this.operationClass = operationClass;
        this.data = data;
        this.uuid = uuid;
        this.methodHashCode = methodHashCode;
    }

    @Override
    public CloudNodeResponsePacket call() throws Exception {
        var operation = (TOperation) operationClass.getDeclaredConstructor().newInstance();
        Method ex = null;
        for (Method m : operationClass.getDeclaredMethods()) {
            if (m.hashCode() == this.methodHashCode) {
                ex = m;
                break;
            }
        }
        System.out.println("Method name " + ex.getName());
        ex.setAccessible(true);
        return new CloudNodeResponsePacket(uuid, Toolkit.Encode(ex.invoke(operation, data)));
    }
}
