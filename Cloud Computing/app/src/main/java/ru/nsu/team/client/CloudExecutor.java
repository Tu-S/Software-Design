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
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CloudExecutor<T, R> {

    private static String host;
    private static int port;
    public static final int COMMAND = 1488;
    public static final int COMMAND_AND_DATA = 322;
    public static final int DATA = 228;
    public static final int COLLECT = 666;
    public static int executors = 1;
    private List<LocalCallable<T, R>> subTasks;
    private ExecutorService exec = Executors.newSingleThreadExecutor();
    private Collection<T> data;

    public CloudExecutor() {
    }

    public static void init(String host, int port) {
        CloudExecutor.host = host;
        CloudExecutor.port = port;
    }

//    public static <T, R> Stream<Object> execute(T data, SerializableFunction<? super T, ? extends R> mapper)
//            throws IOException, ClassNotFoundException {
//        var operationClass = LambdaExtractor.extractClass(mapper);
//        var method = LambdaExtractor.extractMethod(mapper);
//        var hashCode = method.hashCode();
//        System.out.println("method = " + method.getName());
//        System.out.println("operation class = " + operationClass.getCanonicalName());
//        var inClass = data.getClass();
//        System.out.println("inClass = " + inClass.getCanonicalName());
//        var request = new CloudPacket(Agent.loadedClasses, Toolkit.Encode(operationClass),
//                Toolkit.Encode(inClass), Toolkit.Encode(data), true, hashCode);
//        return Arrays.stream(CloudExecutor.serverExchange(request, Object[].class));
//    }
//
//    public static <T, R> Object[] testExecute(T data, SerializableFunction<? super T, ? extends R> mapper)
//            throws IOException, ClassNotFoundException {
//        var operationClass = LambdaExtractor.extractClass(mapper);
//        var method = LambdaExtractor.extractMethod(mapper);
//        var hashCode = method.hashCode();
//        System.out.println("method = " + method.getName());
//        System.out.println("operation class = " + operationClass.getCanonicalName());
//        var inClass = data.getClass();
//        System.out.println("inClass = " + inClass.getCanonicalName());
//        var request = new CloudPacket(Agent.loadedClasses, Toolkit.Encode(operationClass),
//                Toolkit.Encode(inClass), Toolkit.Encode(data), true, hashCode);
//        return CloudExecutor.serverExchange(request);
//    }

    public static <T, R> Object[] testDataCommandExecute(int commandType,T data, SerializableFunction<? super T, ? extends R> mapper)
            throws IOException, ClassNotFoundException {
        if(mapper != null){
            var operationClass = LambdaExtractor.extractClass(mapper);
            var method = LambdaExtractor.extractMethod(mapper);
            var hashCode = method.hashCode();
            System.out.println("method = " + method.getName());
            System.out.println("operation class = " + operationClass.getCanonicalName());
            var inClass = data.getClass();
            System.out.println("inClass = " + inClass.getCanonicalName());
            var request = new CloudPacket(Agent.loadedClasses, Toolkit.Encode(operationClass),
                    Toolkit.Encode(inClass), Toolkit.Encode(data), true, hashCode, commandType);
            return CloudExecutor.serverExchange(request, Object[].class);
        }
        var inClass = data.getClass();
        System.out.println("inClass = " + inClass.getCanonicalName());
        var request = new CloudPacket(Agent.loadedClasses, null,
                Toolkit.Encode(inClass), Toolkit.Encode(data), true, -666, commandType);
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

//    private static <TResponse> TResponse serverExchange(CloudPacket request, Class<TResponse> responseClass)
//            throws IOException, ClassNotFoundException {
//        var socket = new Socket(host, port);
//        var outputStream = new DataOutputStream(socket.getOutputStream());
//        var inputStream = new DataInputStream(socket.getInputStream());
//
//        var encodedRequest = Toolkit.Encode(request);
//        outputStream.writeInt(encodedRequest.length);
//        outputStream.write(encodedRequest);
//        outputStream.flush();
//
//        var responseLength = inputStream.readInt();
//        var response = new byte[responseLength];
//        inputStream.readFully(response);
//
//        inputStream.close();
//        outputStream.close();
//        socket.close();
//
//        return Toolkit.Decode(response, responseClass);
//    }


//    public static <T, R> Stream<Object> cloudMap(Collection<T> data, SerializableFunction<? super T[], ? extends R> mapper, int executorsCount) throws ExecutionException, InterruptedException {
//        var chunks = new LinkedList<ArrayList<T>>();
//        var chunkSize = data.size() / executorsCount + ((data.size() % executorsCount) == 0 ? 0 : 1);
//        var dataList = new ArrayList<>(data);
//
//        for (int i = 0; i < executorsCount; i++) {
//            chunks.add(new ArrayList<T>(dataList.subList(i * chunkSize, (i + 1) * chunkSize)));
//        }
//
//        var subTasks = chunks.stream().map(chunk -> new LocalCallable<T, R>(chunk, mapper)).collect(Collectors.toList());
//        var task = new CloudCallable<T, R>(subTasks);
//        var exec = Executors.newSingleThreadExecutor();
//
//        var result3 = exec.submit(task).get();
//        List<Object> list = new ArrayList<>();
//        for (var el : result3) {
//            list.addAll(Arrays.asList(el));
//        }
//
//        return list.stream();
//    }

    public void testCloudMap(Collection<T> data, SerializableFunction<? super T[], ? extends R> mapper, int executorsCount) throws ExecutionException, InterruptedException {
        this.data = data;
        executors = executorsCount;
        var chunks = new LinkedList<ArrayList<T>>();
        var chunkSize = data.size() / executorsCount + ((data.size() % executorsCount) == 0 ? 0 : 1);
        var dataList = new ArrayList<T>(data);

        for (int i = 0; i < executorsCount; i++) {
            chunks.add(new ArrayList<T>(dataList.subList(i * chunkSize, (i + 1) * chunkSize)));
        }

        subTasks = chunks.stream().map(chunk -> new LocalCallable<T, R>(COMMAND_AND_DATA,chunk, mapper)).collect(Collectors.toList());

    }


    public void loadData(Collection<T> data,int executorsCount) throws ExecutionException, InterruptedException {
        this.data = data;
        executors = executorsCount;
        var chunks = new LinkedList<ArrayList<T>>();
        var chunkSize = data.size() / executorsCount + ((data.size() % executorsCount) == 0 ? 0 : 1);
        var dataList = new ArrayList<T>(data);

        for (int i = 0; i < executorsCount; i++) {
            chunks.add(new ArrayList<T>(dataList.subList(i * chunkSize, (i + 1) * chunkSize)));
        }

        subTasks = chunks.stream().map(chunk -> new LocalCallable<T, R>(DATA,chunk, null)).collect(Collectors.toList());
        collect();
        subTasks.clear();
    }
    public void applyFunction(SerializableFunction<? super T[], ? extends R> mapper)
            throws IOException, ClassNotFoundException {
//        var operationClass = LambdaExtractor.extractClass(mapper);
//        var method = LambdaExtractor.extractMethod(mapper);
//        var hashCode = method.hashCode();
//        System.out.println("method = " + method.getName());
//        System.out.println("operation class = " + operationClass.getCanonicalName());
//        var request = new CloudPacket(Toolkit.Encode(Object[].class), Toolkit.Encode(operationClass), hashCode, COMMAND);
//        CloudExecutor.serverExchange(request);
        var chunks = new LinkedList<ArrayList<T>>();
        var chunkSize = data.size() / executors + ((data.size() % executors == 0 ? 0 : 1));
        var dataList = new ArrayList<T>(data);

        for (int i = 0; i < executors; i++) {
            chunks.add(new ArrayList<T>(dataList.subList(i * chunkSize, (i + 1) * chunkSize)));
        }

        subTasks.addAll(chunks.stream().map(chunk -> new LocalCallable<T, R>(COMMAND,chunk, mapper)).collect(Collectors.toList()));
    }


    private void collectACK(){


    }
    public Object[] collect() throws ExecutionException, InterruptedException {
        var task = new CloudCallable<T, R>(subTasks);
        var result3 = exec.submit(task).get();
        List<Object> list = new ArrayList<>();
        for (var el : result3) {
            list.addAll(Arrays.asList(el));
        }
        exec.shutdown();
        return list.toArray();
    }


}
