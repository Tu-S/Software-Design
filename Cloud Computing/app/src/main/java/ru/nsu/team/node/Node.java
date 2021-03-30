package ru.nsu.team.node;

import ru.nsu.team.packet.CloudNodePacket;
import ru.nsu.team.packet.CloudNodeResponsePacket;
import ru.nsu.team.tools.Toolkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Node {
    private static final String host = "localhost";
    private static final int port = 18226;

    public static void main(String[] args) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        var socket = new Socket(host, port);
        var inputStream = new DataInputStream(socket.getInputStream());
        var outputStream = new DataOutputStream(socket.getOutputStream());
        var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        var tasks = new LinkedList<Future<CloudNodeResponsePacket>>();
        while (!socket.isClosed()) {
            if (inputStream.available() > 0) {
                var requestBuffer = new byte[inputStream.readInt()];
                inputStream.readFully(requestBuffer);
                var request = Toolkit.Decode(requestBuffer, CloudNodePacket.class);
                var classInjector = new ClassInjector(Thread.currentThread().getContextClassLoader());
                classInjector.InjectClasses(request.input.classCodes);
                var operationClass = Toolkit.Decode(request.input.operationClass, Class.class);
                var dataClass = Toolkit.Decode(request.input.dataClass, Class.class);
                var data = Toolkit.Decode(request.input.data, dataClass);
                var task = new TaskExecutor(operationClass, request.input.hashCode, data, request.uuid);
                var future = executor.submit(task);
                tasks.add(future);
                System.out.println("Task accepted: " + request.uuid.toString());
            }
            var doneTasks = tasks.stream().filter(Future::isDone).collect(Collectors.toList());
            for (var doneTask : doneTasks) {
                tasks.remove(doneTask);
                var response = doneTask.get();
                var responseBuffer = Toolkit.Encode(response);
                outputStream.writeInt(responseBuffer.length);
                outputStream.write(responseBuffer);
                outputStream.flush();
                System.out.println("Task done: " + response.uuid.toString());
            }
            Thread.sleep(1000);
        }
    }
}
