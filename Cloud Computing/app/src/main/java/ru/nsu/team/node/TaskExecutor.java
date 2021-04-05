package ru.nsu.team.node;


import org.checkerframework.checker.units.qual.A;
import ru.nsu.team.packet.CloudNodeResponsePacket;
import ru.nsu.team.packet.CloudNodeResultPacket;
import ru.nsu.team.packet.CloudPacket;
import ru.nsu.team.packet.CloudPacketSet;
import ru.nsu.team.tools.KeyValuePair;
import ru.nsu.team.tools.Toolkit;

import javax.tools.Tool;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;

public class TaskExecutor implements Callable<CloudNodeResultPacket> {
    //    //private final Class<TOperation> operationClass;
//    private T data;
//    private Object[] res;
//    private UUID uuid;
    private CloudPacketSet set;
    //private final int methodHashCode;


//    public TaskExecutor(final Class<TOperation> operationClass, int methodHashCode,
//                        final T data, final UUID uuid) {
//        //this.operationClass = operationClass;
//        this.data = data;
//        this.uuid = uuid;
//        //this.methodHashCode = methodHashCode;
//    }

//    public TaskExecutor(final T data, final UUID uuid) {
//        this.data = data;
//        this.uuid = uuid;
//    }

    public TaskExecutor(CloudPacketSet set) {
        this.set = set;

    }

//    public void addMapper(int hash, Class<TOperation> mapper) {
//        this.mappers.add(new KeyValuePair<>(hash, mapper));
//    }

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

//            var operationClass = Toolkit.Decode(packet.operationClass, Class.class);
//            var operation = (TOperation) operationClass.getDeclaredConstructor().newInstance();
//            Method ex = null;
//            for (Method m : operationClass.getDeclaredMethods()) {
//                if (m.hashCode() == packet.hashCode) {
//                    ex = m;
//                    break;
//                }
//            }
//            System.out.println("Method name " + ex.getName());
//            ex.setAccessible(true);
//            res = new Object[] {ex.invoke(operation, res)};

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
            var o = ex.invoke(operation, data);
            return o;
        }

    }
}
