package ru.nsu.team.client;

import ru.nsu.team.agent.Agent;
import ru.nsu.team.packet.CloudPacket;
import ru.nsu.team.packet.CloudPacketSet;
import ru.nsu.team.tools.Toolkit;
import ru.nsu.team.tools.lambda.LambdaExtractor;
import ru.nsu.team.tools.lambda.SerializableFunction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CloudExecutor<T, R> {

    private static String host;
    private static int port;
    public static final int COMMAND = 1488;
    public static int executors = 1;
    private List<LocalCallable<T, R>> subTasks;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private Collection<T> data;
    private final List<SerializableFunction<? super T[], ? extends R>> functions;

    public CloudExecutor() {
        functions = new ArrayList<>();
    }

    public static void init(String host, int port) {
        CloudExecutor.host = host;
        CloudExecutor.port = port;
    }

    public static <T, R> Object[] dataCommandExecute(int commandType, T data, List<SerializableFunction<? super T, ? extends R>> mappers)
            throws IOException, ClassNotFoundException {
        var packetSet = new CloudPacketSet(commandType);
        if (mappers != null) {
            for (var mapper : mappers) {
                var operationClass = LambdaExtractor.extractClass(mapper);
                var method = LambdaExtractor.extractMethod(mapper);
                var hashCode = method.hashCode();
                System.out.println("method = " + method.getName());
                System.out.println("operation class = " + operationClass.getCanonicalName());
                var inClass = data.getClass();
                System.out.println("inClass = " + inClass.getCanonicalName());
                var request = new CloudPacket(Agent.loadedClasses, Toolkit.Encode(operationClass),
                        Toolkit.Encode(inClass), Toolkit.Encode(data), true, hashCode, commandType);
                packetSet.packets.add(request);
            }
        } else {
            var inClass = data.getClass();
            System.out.println("inClass = " + inClass.getCanonicalName());
            var request = new CloudPacket(Agent.loadedClasses, null,
                    Toolkit.Encode(inClass), Toolkit.Encode(data), true, -666, commandType);
            packetSet.packets.add(request);
        }
        return CloudExecutor.serverExchange(packetSet, Object[].class);

    }


    private static <TResponse> TResponse serverExchange(CloudPacketSet request, Class<TResponse> responseClass)
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

    public void loadData(Collection<T> data, int executorsCount) {
        this.data = data;
        executors = executorsCount;
    }

    public void applyFunction(SerializableFunction<? super T[], ? extends R> mapper) {
        functions.add(mapper);
    }

    private void createTasks() {
        var chunks = new LinkedList<ArrayList<T>>();
        var chunkSize = data.size() / executors + ((data.size() % executors) == 0 ? 0 : 1);
        var dataList = new ArrayList<>(data);

        for (int i = 0; i < executors; i++) {
            chunks.add(new ArrayList<>(dataList.subList(i * chunkSize, (i + 1) * chunkSize)));
        }

        subTasks = chunks.stream().map(chunk -> new LocalCallable<>(COMMAND, chunk, functions)).collect(Collectors.toList());

    }

    public Object[] collect() throws ExecutionException, InterruptedException {
        createTasks();
        var task = new CloudCallable<>(subTasks);
        var result3 = exec.submit(task).get();
        List<Object> list = new ArrayList<>();
        for (var el : result3) {
            list.addAll(Arrays.asList(el));

        }
        exec.shutdown();
        return list.toArray();
    }
}
