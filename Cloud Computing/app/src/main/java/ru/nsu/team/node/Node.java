package ru.nsu.team.node;

import org.checkerframework.framework.qual.PreconditionAnnotation;
import ru.nsu.team.client.CloudExecutor;
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
        int breakCounter = -1;
        CloudNodePacket request = null;
        boolean loaded = false;
        ArrayDeque<CloudPacketSet> commandQueue = new ArrayDeque<>();
        var socket = new Socket(host, port);
        var inputStream = new DataInputStream(socket.getInputStream());
        var outputStream = new DataOutputStream(socket.getOutputStream());
        var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        var tasks = new LinkedList<Future<CloudNodeResultPacket>>();
        var dataACK = new LinkedList<Future<Integer>>();

        while (!socket.isClosed()) {

            if (inputStream.available() > 0) {
                var requestBuffer = new byte[inputStream.readInt()];
                inputStream.readFully(requestBuffer);
                request = Toolkit.Decode(requestBuffer, CloudNodePacket.class);
                switch (request.input.command) {
                    case CloudExecutor.DATA: {
                        for (var p : request.input.packets) {
                            var dataClass = Toolkit.Decode(p.dataClass, Class.class);
                            var data = Toolkit.Decode(p.data, dataClass);
                            res.add((Object[]) data);
                            var task = new ACK();
                            var future = executor.submit(task);
                            dataACK.add(future);
                        }
                        break;
                    }
                    case CloudExecutor.COMMAND: {
                        commandQueue.add(request.input);
//                        var operationClass = Toolkit.Decode(request.input.dataClass, Class.class);//Toolkit.Decode(request.input.operationClass, Class.class);
//                        commandQueue.add(operationClass);
//                        for (var el : res) {
//                            var task = new TaskExecutor(operationClass, request.input.hashCode, el, request.uuid);
//                            var future = executor.submit(task);
//                            tasks.add(future);
//                        }
//                        System.out.println("added command");
                        //System.out.println("Task accepted: " + request.uuid.toString());

                        break;
                    }
                }

            }
            while (!commandQueue.isEmpty()) {
                if (res.isEmpty()) {
                    break;
                }
                var req = commandQueue.poll();
                if (req.command == CloudExecutor.COMMAND) {
                    var task = new TaskExecutor(req);
                    var future = executor.submit(task);
                    tasks.add(future);
                }
            }
            if (!tasks.isEmpty()) {
                var doneTasks = tasks.stream().filter(Future::isDone).collect(Collectors.toList());
                for (var doneTask : doneTasks) {
                    tasks.remove(doneTask);
                    var response = doneTask.get();
                    res.add(response.answer);
                    var responseBuffer = Toolkit.Encode(new CloudNodeResponsePacket(response.uuid, Toolkit.Encode(response.answer)));
                    outputStream.writeInt(responseBuffer.length);
                    outputStream.write(responseBuffer);
                    outputStream.flush();
                    System.out.println("Task done: " + response.uuid.toString());
                }
            }
            if (!dataACK.isEmpty()) {
                var doneACK = dataACK.stream().filter(Future::isDone).collect(Collectors.toList());
                for (var doneAck : doneACK) {
                    dataACK.remove(doneAck);
                    doneAck.get();
                    var responseBuffer = Toolkit.Encode(new CloudNodeResponsePacket(null, Toolkit.Encode(new Object[0])));
                    outputStream.writeInt(responseBuffer.length);
                    outputStream.write(responseBuffer);
                    outputStream.flush();
                    System.out.println("Task done: " + doneAck.hashCode());
                }
            }

//            for (var curTask : tasks) {
//                res.add(curTask.get().answer);
//            }

            //curResult.answer = res.toArray();
            //curResult.uuid = request.uuid;
            //var doneTasks = tasks.stream().filter(Future::isDone).collect(Collectors.toList());
            //Thread.sleep(1000);
        }
    }
}
