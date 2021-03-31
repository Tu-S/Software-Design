package ru.nsu.team.client;

import ru.nsu.team.Person;
import ru.nsu.team.agent.Agent;
import ru.nsu.team.packet.CloudPacket;

import ru.nsu.team.tools.Toolkit;
import ru.nsu.team.tools.lambda.SerializableFunction;
import ru.nsu.team.tools.lambda.LambdaExtractor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CloudExecutor {

    private static String host;
    private static int port;

    private CloudExecutor() {
    }

    public static void init(String host, int port) {
        CloudExecutor.host = host;
        CloudExecutor.port = port;
    }

    public static <T, R> Stream<Object> execute(T data, SerializableFunction<? super T, ? extends R> mapper)
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
        return Arrays.stream(CloudExecutor.serverExchange(request, Object[].class));
    }

    public static <T, R> Object[] testExecute(Object[] data, SerializableFunction<? super T, ? extends R> mapper)
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


    public static <T, R> void Execute(Collection<T> data, SerializableFunction<? super T, ? extends R> mapper, int executorsCount) throws ExecutionException, InterruptedException {

        var chunks = new LinkedList<ArrayList<T>>();
        var chunkSize = data.size() / executorsCount + ((data.size() % executorsCount) == 0 ? 0 : 1);
        var dataList = new ArrayList<>(data);
        for (int i = 0; i < executorsCount; i++) {
            chunks.add(new ArrayList<>(dataList.subList(i * chunkSize, (i + 1) * chunkSize)));
        }

        var subTasks = chunks.stream().map(chunk -> new LocalCallable<T,R>(chunk, mapper)).collect(Collectors.toList());
        var task = new JavaMpiCallable<T,R>(subTasks);
        var exec = Executors.newSingleThreadExecutor();

        var result3 = exec.submit(task).get();
        for (var el : result3) {
            System.out.println(el);
        }
        exec.shutdown();
    }


}
