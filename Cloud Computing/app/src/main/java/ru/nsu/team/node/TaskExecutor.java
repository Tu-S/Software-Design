package ru.nsu.team.node;


import ru.nsu.team.packet.CloudNodeResponsePacket;
import ru.nsu.team.tools.Toolkit;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

public class TaskExecutor<TIn, TOut, TOperation> implements Callable<CloudNodeResponsePacket> {
    private Class<TOperation> operationClass;
    private TIn data;
    private UUID uuid;
    private int methodHashCode;

    public TaskExecutor(final Class<TOperation> operationClass, int methodHashCode, final TIn data, final UUID uuid) {
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
