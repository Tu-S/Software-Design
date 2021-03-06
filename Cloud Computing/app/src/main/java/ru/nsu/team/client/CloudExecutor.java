package ru.nsu.team.client;

import ru.nsu.team.agent.Agent;
import ru.nsu.team.packages.PackageToServer;
import ru.nsu.team.tools.Toolkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
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

        return CloudExecutor.<PackageToServer, TOut>serverExchange(request, outClass);
    }

    public static <TIn, R> Stream<R> cloudMap(Collection<TIn> data, Function<? super TIn,? extends R> mapper){
            System.out.println("in cloud map " + mapper.getClass());
            return  data.stream().map(mapper);
    }

    public static <TIn extends Serializable, TOperation extends CloudMapOperation<TIn, TOut>, TOut extends Serializable> Callable<? extends List<TOut>> execute(Collection<TIn> data, Class<TOperation> operationClass, int executorsCount) {
        var chunks = new LinkedList<ArrayList<TIn>>();
        var chunkSize = data.size() / executorsCount + ((data.size() % executorsCount) == 0 ? 0 : 1);
        var dataList = new ArrayList<>(data);
        for (int i = 0; i < executorsCount; i++) {
            chunks.add(new ArrayList<>(dataList.subList(i * chunkSize, (i + 1) * chunkSize)));
        }

        var subTasks = chunks.stream().map(chunk -> new LocalCallable<>(chunk, (Class<ArrayList<TIn>>) new ArrayList<TIn>().getClass(), operationClass, (Class<ArrayList<TOut>>) new ArrayList<TOut>().getClass())).collect(Collectors.toList());
        return new CloudCallable<TOut>(subTasks);
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

    private static class LocalCallable<TIn extends Serializable, TOperation extends CloudOperation<TIn, TOut>, TOut extends Serializable> implements Callable<TOut> {

        private TIn data;
        private Class<TIn> inClass;
        private Class<TOperation> operationClass;
        private Class<TOut> outClass;

        public LocalCallable(TIn data, Class<TIn> inClass, Class<TOperation> operationClass, Class<TOut> outClass) {
            this.data = data;
            this.inClass = inClass;
            this.operationClass = operationClass;
            this.outClass = outClass;
        }

        @Override
        public TOut call() throws Exception {
            return CloudExecutor.<TIn, TOperation, TOut>execute(data, inClass, operationClass, outClass);
        }
    }
}
