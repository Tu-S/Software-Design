package my.java.mpi.node;

import my.java.mpi.common.JavaMpiUtils;
import my.java.mpi.server.dtos.JavaMpiNodeDto;
import my.java.mpi.server.dtos.JavaMpiNodeResponseDto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Node {

    private static String host = "localhost";
    private static int port = 18226;

    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, ExecutionException, InterruptedException {
        var socket = new Socket(host, port);
        var outputStream = new DataOutputStream(socket.getOutputStream());
        var inputStream = new DataInputStream(socket.getInputStream());
        var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        var tasksList = new LinkedList<Future<JavaMpiNodeResponseDto>>();
        while (!socket.isClosed()) {
            if (inputStream.available() > 0) {
                var requestBuffer = new byte[inputStream.readInt()];
                inputStream.readFully(requestBuffer);
                var request = JavaMpiUtils.<JavaMpiNodeDto>Decode(requestBuffer, JavaMpiNodeDto.class);
                var classInjector = new ClassInjector(Thread.currentThread().getContextClassLoader());
                classInjector.InjectClasses(request.input.classCodes);
                var operationClass = JavaMpiUtils.Decode(request.input.operationClass, Class.class);
                var dataClass = JavaMpiUtils.Decode(request.input.dataClass, Class.class);
                var data = JavaMpiUtils.Decode(request.input.data, dataClass);
                var task = new TaskExecutor(operationClass, data, request.id);
                var future = executor.submit(task);
                tasksList.add(future);
                System.out.println("Task accepted: " + request.id.toString());
            }
            var doneTasks = tasksList.stream().filter(Future::isDone).collect(Collectors.toList());
            for (var doneTask : doneTasks) {
                tasksList.remove(doneTask);
                var response = doneTask.get();
                var responseBuffer = JavaMpiUtils.Encode(response);
                outputStream.writeInt(responseBuffer.length);
                outputStream.write(responseBuffer);
                outputStream.flush();
                System.out.println("Task done: " + response.id.toString());
            }
            Thread.sleep(1000);
        }
    }

}
