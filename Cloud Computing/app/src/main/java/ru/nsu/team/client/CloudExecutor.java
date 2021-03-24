package ru.nsu.team.client;

import ru.nsu.team.agent.Agent;
import ru.nsu.team.packet.CloudPacket;

import ru.nsu.team.tools.Toolkit;
import ru.nsu.team.tools.lambda.SerializableFunction;
import ru.nsu.team.tools.lambda.LambdaExtractor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CloudExecutor {

  private static String host;
  private static int port;

  private CloudExecutor() {
  }

  public static void init(String host, int port) {
    CloudExecutor.host = host;
    CloudExecutor.port = port;
  }

  public static <T, R> Object[] execute(T data, SerializableFunction<? super T, ? extends R> mapper)
      throws IOException, ClassNotFoundException {
    var operationClass = LambdaExtractor.extractClass(mapper);
    var method = LambdaExtractor.extractMethod(mapper);
    var hashCode = method.hashCode();
    System.out.println("method = " + method.getName());
    System.out.println("operation class = " + operationClass.getCanonicalName());
    var inClass = data.getClass();
    System.out.println("inClass = " + inClass.getCanonicalName());
    var request = new CloudPacket(Agent.loadedClasses, Toolkit.Encode(operationClass),
        Toolkit.Encode(inClass), Toolkit.Encode(data), true, hashCode);
    return CloudExecutor.serverExchange(request, Object[].class);
  }

  private static <TResponse> TResponse serverExchange(CloudPacket request, Class<TResponse> responseClass)
      throws IOException, ClassNotFoundException {
    var socket = new Socket(host, port);
    var outputStream = new DataOutputStream(socket.getOutputStream());
    var inputStream = new DataInputStream(socket.getInputStream());

    var encodedRequest = Toolkit.Encode(request);

    outputStream.writeInt(encodedRequest.length);
    outputStream.write(encodedRequest);
    outputStream.flush();

    var responseLength = inputStream.readInt();
    var response = new byte[responseLength];
    inputStream.readFully(response);

    inputStream.close();
    outputStream.close();
    socket.close();

    return Toolkit.Decode(response, responseClass);
  }
}
