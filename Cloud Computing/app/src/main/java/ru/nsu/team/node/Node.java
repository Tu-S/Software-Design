package ru.nsu.team.node;

import ru.nsu.team.packet.*;
import ru.nsu.team.tools.Toolkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Node {
    private static final String host = "localhost";
    private static final int port = 18226;

    public static void main(String[] args) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        Queue<Object[]> res = new ArrayDeque<>();
        CloudNodePacket request;
        var socket = new Socket(host, port);
        var inputStream = new DataInputStream(socket.getInputStream());
        var outputStream = new DataOutputStream(socket.getOutputStream());
        var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        var tasks = new LinkedList<Future<CloudNodeResultPacket>>();

        while (!socket.isClosed()) {

            if (inputStream.available() > 0) {
                var requestBuffer = new byte[inputStream.readInt()];
                inputStream.readFully(requestBuffer);
                request = Toolkit.Decode(requestBuffer, CloudNodePacket.class);
                var classInjector = new ClassInjector(Thread.currentThread().getContextClassLoader());
                classInjector.injectClasses(request.input.packets.get(0).classCodes);
                var task = new TaskExecutor(request.input);
                var future = executor.submit(task);
                tasks.add(future);
                System.out.println("Task accepted: " + request.uuid.toString());
            }
            var doneTasks = tasks.stream().filter(Future::isDone).collect(Collectors.toList());
            for (var doneTask : doneTasks) {
                tasks.remove(doneTask);
                var response = doneTask.get();
                res.add(response.answer);
                var responseBuffer = Toolkit.Encode(new CloudNodeResponsePacket(response.uuid, Toolkit.Encode(response.answer)));
                outputStream.writeInt(responseBuffer.length);
                outputStream.write(responseBuffer);
                outputStream.flush();
                System.out.println("Task done: ");
            }

        }
    }
}
