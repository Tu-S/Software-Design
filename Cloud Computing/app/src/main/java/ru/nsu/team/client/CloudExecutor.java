package ru.nsu.team.client;

import ru.nsu.team.agent.Agent;
import ru.nsu.team.annotation.Remote;
import ru.nsu.team.packages.PackageToServer;

import ru.nsu.team.tools.Toolkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class CloudExecutor {

    private static String host;
    private static int port;

    private CloudExecutor() {
    }

    public static void init(String host, int port) throws IOException {
        CloudExecutor.host = host;
        CloudExecutor.port = port;
    }

    public static <TIn extends Serializable, TOperation extends CloudOperation<TIn, TOut>, TOut extends Serializable> TOut execute(TIn data, Class<TIn> inClass, Class<TOperation> operationClass, Class<TOut> outClass) throws ClassNotFoundException, IOException {

        var request = new PackageToServer(Agent.loadedClasses, Toolkit.Encode(operationClass), Toolkit.Encode(inClass), Toolkit.Encode(data), true);

        return CloudExecutor.serverExchange(request, outClass);
    }


    //    public static <TIn extends Serializable, R extends Serializable> Stream<R> cloudMap(List<TIn> data, Function<TIn, R> mapper) {
//        /*хз как получить нужные типы, с входными параметрами +- ок, но вот с выходным хз. Хз как хочет мигина, но эту херь так просто не сделаешь*/
//        System.out.println("in cloud map " + mapper.getClass());
//        System.out.println("in elem class = " + data.get(0).getClass());
//        var inArr = (TIn[]) java.lang.reflect.Array.newInstance(data.get(0).getClass(), data.size());
//        Class<? extends Serializable[]> cl = inArr.getClass();
//        System.out.println("in array class = " + cl.getSimpleName());
//        var out = mapper.apply(data.get(0));
//        var outArr = (R[]) java.lang.reflect.Array.newInstance(out.getClass(), data.size());
//        System.out.println("out elem class = " + out.getClass().getSimpleName());
//        System.out.println("out array class = " + outArr.getClass().getSimpleName());
//        var wtf = new Integer[10];
//        //execute(wtf,inArr.getClass(),TestOperation.class,outArr.getClass());
//        //Sergey's test version
//        return data.stream().map(mapper);
//    }
    public static <TIn extends Serializable, TOperation> Stream<?> testExecute(TIn data, Class<TOperation> operationClass) throws IOException, ClassNotFoundException {
        System.out.println("input class = " + data.getClass().getCanonicalName());
        int executorsCount = 3;
        Method ex = null;
        for (Method m : operationClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Remote.class)) {
                ex = m;
                break;
            }
        }

        for (Class<?> inClass : ex.getParameterTypes()) {
            System.out.println(inClass.getName());
            Class<?> outClass = ex.getReturnType();
            Class<?> outType = outClass.getComponentType();
            var request = new PackageToServer(Agent.loadedClasses, Toolkit.testEncode(operationClass), Toolkit.testEncode(inClass), Toolkit.testEncode(data), true);
            var resFromServer = CloudExecutor.testServerExchange(request, Object[].class);
            return Arrays.stream(resFromServer);
        }
        return null;
    }

    public static <TIn, R> Object[] staticExecute(TIn data, Function<? super TIn, ? extends R> mapper) throws IOException, ClassNotFoundException {
        var operationClass = mapper.getClass();
        System.out.println("operation class = " + operationClass.getCanonicalName());
        var inClass = data.getClass();
        System.out.println("inClass = " + inClass.getCanonicalName());
        var request = new PackageToServer(Agent.loadedClasses, Toolkit.testEncode(operationClass), Toolkit.testEncode(inClass), Toolkit.testEncode(data), true);
        var resFromServer = CloudExecutor.testServerExchange(request, Object[].class);
        return resFromServer;
    }

    private static <TResponse> TResponse testServerExchange(PackageToServer request, Class<TResponse> responseClass) throws IOException, ClassNotFoundException {
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

        return Toolkit.testDecode(response, responseClass);
    }


    private static <TRequest extends Serializable, TResponse extends Serializable> TResponse serverExchange(TRequest request, Class<TResponse> responseClass) throws IOException, ClassNotFoundException {
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

        return Toolkit.<TResponse>Decode(response, responseClass);
    }
}
