package ru.nsu.team.node;


import ru.nsu.team.packet.CloudNodeResultPacket;
import ru.nsu.team.packet.CloudPacketSet;
import ru.nsu.team.tools.Toolkit;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

public class TaskExecutor implements Callable<CloudNodeResultPacket> {
    private final CloudPacketSet set;
    public TaskExecutor(CloudPacketSet set) {
        this.set = set;

    }

    @Override
    public CloudNodeResultPacket call() throws Exception {
        var pack = set.packets.get(0);
        var dataClass = Toolkit.Decode(pack.dataClass, Class.class);
        var data = Toolkit.Decode(pack.data, dataClass);
        var classInjector = new ClassInjector(Thread.currentThread().getContextClassLoader());
        classInjector.injectClasses(pack.classCodes);
        for (var packet : set.packets) {
            var operationClass = Toolkit.Decode(pack.operationClass, Class.class);
            data = new Executor(operationClass, packet.hashCode, data, null).call();
        }
        return new CloudNodeResultPacket(null, (Object[]) data);


    }

    class Executor<T, TOperation> {

        private final Class<TOperation> operationClass;
        private final T data;
        private final UUID uuid;
        private final int methodHashCode;

        public Executor(final Class<TOperation> operationClass, int methodHashCode,
                        final T data, final UUID uuid) {
            this.operationClass = operationClass;
            this.data = data;
            this.uuid = uuid;
            this.methodHashCode = methodHashCode;
        }

        public Object call() throws Exception {
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
            return ex.invoke(operation, data);
        }

    }
}
