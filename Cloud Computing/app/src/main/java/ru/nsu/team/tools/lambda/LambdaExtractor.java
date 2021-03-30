package ru.nsu.team.tools.lambda;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class LambdaExtractor {
  public static <T, R> Class<?> extractClass(SerializableFunction<T, R> lambda) {
    return getClass(lambda);
  }

  public static <T, R> Method extractMethod(SerializableFunction<T, R> lambda) {
    return getMethod(lambda);
  }

  private static Class<?> getClass(Object lambda) {
    SerializedLambda serialized = serialize(lambda);
    return getContainingClass(serialized);
  }

  private static Method getMethod(Object lambda) {
    SerializedLambda serialized = serialize(lambda);
    Class<?> containingClass = getContainingClass(serialized);
    return Arrays.stream(containingClass.getDeclaredMethods())
        .filter(method -> Objects.equals(method.getName(), serialized.getImplMethodName()))
        .findFirst()
        .orElseThrow(RuntimeException::new);
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
