package my.java.mpi.client;

import my.java.mpi.agent.JavaMpiAgent;
import my.java.mpi.annoatation.Remote;
import my.java.mpi.common.JavaMpiUtils;
import my.java.mpi.server.dtos.JavaMpiDto;
import my.java.mpi.test.Person;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JavaMpi {

    private static String host;
    private static int port;

    private JavaMpi() {
    }

    public static void Init(String host, int port) throws IOException {
        JavaMpi.host = host;
        JavaMpi.port = port;
    }

//    public static <TIn extends Serializable, TOperation extends AJavaMpiOperation<TIn, TOut>, TOut extends Serializable> TOut Execute(TIn data, Class<TIn> inClass, Class<TOperation> operationClass, Class<TOut> outClass) throws ClassNotFoundException, IOException {
//
//        var request = new JavaMpiDto(JavaMpiAgent.allLoadedClasses, JavaMpiUtils.Encode(operationClass), JavaMpiUtils.Encode(inClass), JavaMpiUtils.Encode(data), true);
//
//        return JavaMpi.<JavaMpiDto, TOut>ServerExchange(request, outClass);
//    }
    public static <TIn extends Serializable,TOperation> Stream<?> testExecute(TIn data, Class<TOperation> operationClass) throws IOException, ClassNotFoundException {
        System.out.println("input class = " + data.getClass().getCanonicalName());
        int executorsCount = 3;
        Method ex = null;
        for(Method m : operationClass.getDeclaredMethods()){
            if(m.isAnnotationPresent(Remote.class)){
                ex = m;
                break;
            }
        }

        for(Class<?> inClass :ex.getParameterTypes() ){
            System.out.println(inClass.getName());
            Class<?> outClass = ex.getReturnType();
            Class<?> outType = outClass.getComponentType();
            var request = new JavaMpiDto(JavaMpiAgent.allLoadedClasses, JavaMpiUtils.testEncode(operationClass), JavaMpiUtils.testEncode(inClass), JavaMpiUtils.testEncode(data), true);
            var resFromServer = JavaMpi.testServerExchange(request, Object[].class);
            return Arrays.stream(resFromServer);
        }
        return null;
    }


    public static <TIn, R> Object[] cloudMap(TIn data, Function<? super TIn,? extends R> mapper) throws IOException, ClassNotFoundException {
        var operationClass = mapper.getClass().getSuperclass();
        System.out.println("operation class = " + operationClass.getName());
        var inClass = data.getClass();
        System.out.println("inClass = " + inClass.getCanonicalName());
        var request = new JavaMpiDto(JavaMpiAgent.allLoadedClasses, JavaMpiUtils.testEncode(operationClass), JavaMpiUtils.testEncode(inClass), JavaMpiUtils.testEncode(data), true);
        var resFromServer = JavaMpi.testServerExchange(request,Object[].class);
        return resFromServer;
    }


//    public static <TIn extends Serializable, TOperation extends AJavaMpiMapOperation<TIn, TOut>, TOut extends Serializable> Callable<? extends List<TOut>> Execute(Collection<TIn> data, Class<TOperation> operationClass, int executorsCount) {
//        var executor = Executors.newFixedThreadPool(executorsCount);
//        var chunks = new LinkedList<ArrayList<TIn>>();
//        var chunkSize = data.size() / executorsCount + ((data.size() % executorsCount) == 0 ? 0 : 1);
//        var dataList = new ArrayList<>(data);
//        for (int i = 0; i < executorsCount; i++) {
//            chunks.add(new ArrayList<>(dataList.subList(i * chunkSize, (i + 1) * chunkSize)));
//        }
//
//        var subTasks = chunks.stream().map(chunk -> new LocalCallable<>(chunk, (Class<ArrayList<TIn>>) new ArrayList<TIn>().getClass(), operationClass, (Class<ArrayList<TOut>>) new ArrayList<TOut>().getClass())).collect(Collectors.toList());
//        return new JavaMpiCallable<TOut>(subTasks);
//    }



//    private static class LocalCallable<TIn extends Serializable, TOperation extends AJavaMpiOperation<TIn, TOut>, TOut extends Serializable> implements Callable<TOut> {
//
//        private TIn data;
//        private Class<TIn> inClass;
//        private Class<TOperation> operationClass;
//        private Class<TOut> outClass;
//
//        public LocalCallable(TIn data, Class<TIn> inClass, Class<TOperation> operationClass, Class<TOut> outClass) {
//            this.data = data;
//            this.inClass = inClass;
//            this.operationClass = operationClass;
//            this.outClass = outClass;
//        }
//
//        @Override
//        public TOut call() throws Exception {
//            return JavaMpi.<TIn, TOperation, TOut>Execute(data, inClass, operationClass, outClass);
//        }
//    }

//    private static <TRequest extends Serializable, TResponse extends Serializable> TResponse ServerExchange(TRequest request, Class<TResponse> responseClass) throws IOException, ClassNotFoundException {
//        var socket = new Socket(host, port);
//        var outputStream = new DataOutputStream(socket.getOutputStream());
//        var inputStream = new DataInputStream(socket.getInputStream());
//
//        var encodedRequest = JavaMpiUtils.Encode(request);
//
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
//        return JavaMpiUtils.<TResponse>Decode(response, responseClass);
//    }

    private static <TResponse> TResponse testServerExchange(JavaMpiDto request, Class<TResponse> responseClass) throws IOException, ClassNotFoundException {
        var socket = new Socket(host, port);
        var outputStream = new DataOutputStream(socket.getOutputStream());
        var inputStream = new DataInputStream(socket.getInputStream());

        var encodedRequest = JavaMpiUtils.testEncode(request);

        outputStream.writeInt(encodedRequest.length);
        outputStream.write(encodedRequest);
        outputStream.flush();

        var responseLength = inputStream.readInt();
        var response = new byte[responseLength];
        inputStream.readFully(response);

        inputStream.close();
        outputStream.close();
        socket.close();

        return JavaMpiUtils.testDecode(response, responseClass);
    }
}
