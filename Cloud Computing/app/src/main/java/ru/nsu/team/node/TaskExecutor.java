package ru.nsu.team.node;

import ru.nsu.team.annotation.Remote;
import ru.nsu.team.packet.CloudNodeResponsePacket;
import ru.nsu.team.tools.Toolkit;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

public class TaskExecutor <TIn extends Serializable, TOut extends Serializable, TOperation> implements Callable<CloudNodeResponsePacket> {
  private Class<TOperation> operationClass;
  private TIn data;
  private UUID uuid;

  public TaskExecutor(final Class<TOperation> operationClass, final TIn data, final UUID uuid) {
    this.operationClass = operationClass;
    this.data = data;
    this.uuid = uuid;
  }

  @Override
  public CloudNodeResponsePacket call() throws Exception {
    var operation = (TOperation) operationClass.getDeclaredConstructor().newInstance();
    Method ex = null;
    for (Method m : operationClass.getDeclaredMethods()) {
      if (m.isAnnotationPresent(Remote.class)) {
        ex = m;
        break;
      }
    }
    System.out.println("Method name " + ex.getName());
    return new CloudNodeResponsePacket(uuid, Toolkit.testEncode(ex.invoke(operation, data)));
  }
}
