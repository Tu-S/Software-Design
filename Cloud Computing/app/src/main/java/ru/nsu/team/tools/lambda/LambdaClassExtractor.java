package ru.nsu.team.tools.lambda;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class LambdaClassExtractor {
  public static <T, R> Class<?> extract(SerializableFunction<T, R> lambda) {
    return getClass(lambda);
  }

  private static Class<?> getClass(Object lambda) {
    SerializedLambda serialized = serialize(lambda);
    return getContainingClass(serialized);
  }

  private static SerializedLambda serialize(Object lambda) {
    try {
      Method writeMethod = lambda.getClass().getDeclaredMethod("writeReplace");
      writeMethod.setAccessible(true);
      return (SerializedLambda) writeMethod.invoke(lambda);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Class<?> getContainingClass(SerializedLambda lambda) {
    try {
      String className = lambda.getImplClass().replaceAll("/", ".");
      return Class.forName(className);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
