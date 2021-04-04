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
        CloudNodeResultPacket curResult = new CloudNodeResultPacket();
        Queue<Object[]> res = new ArrayDeque<>();
        int breakCounter = -1;
        CloudNodePacket request = null;
        boolean loaded = false;
        ArrayDeque<CloudPacket> commandQueue = new ArrayDeque<>();
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
                    case CloudExecutor.DATA:{
                        var dataClass = Toolkit.Decode(request.input.dataClass, Class.class);
                        var data = Toolkit.Decode(request.input.data, dataClass);
                        res.add((Object[]) data);
                        var task = new ACK();
                        var future = executor.submit(task);
                        dataACK.add(future);
                        break;
                    }
                    case CloudExecutor.COMMAND: {
                        commandQueue.addLast(request.input);
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
                    case CloudExecutor.COMMAND_AND_DATA: {

                        commandQueue.addFirst(request.input);
//                        var classInjector = new ClassInjector(Thread.currentThread().getContextClassLoader());
//                        classInjector.injectClasses(request.input.classCodes);
//                        var operationClass = Toolkit.Decode(request.input.operationClass, Class.class);
//                        var dataClass = Toolkit.Decode(request.input.dataClass, Class.class);
//                        var data = Toolkit.Decode(request.input.data, dataClass);
//                        var task = new TaskExecutor(operationClass, request.input.hashCode, data, request.uuid);
//                        var future = executor.submit(task);
//                        tasks.add(future);
//                        System.out.println("added command and data");
//                        System.out.println("Task accepted: " + request.uuid.toString());
                        break;
                    }
//                    case CloudExecutor.COLLECT: {
//                        List<Object> ans = new ArrayList<>();
//                        for (var el : res) {
//                            for (var e : el) {
//                                ans.add(e);
//                            }
//                        }
//                        var responseBuffer = Toolkit.Encode(ans.toArray());
//                        outputStream.writeInt(responseBuffer.length);
//                        outputStream.write(responseBuffer);
//                        outputStream.flush();
//                        System.out.println("Task done: " + curResult.uuid.toString());
//
//
//                        break;
//                    }

                }

            }
            while (!commandQueue.isEmpty()) {
                if(breakCounter == commandQueue.size()){
                    breakCounter = 0;
                    break;
                }
                var req = commandQueue.poll();

                switch (req.command) {
                    case CloudExecutor.COMMAND: {
                        if(res.isEmpty()){
                            breakCounter++;
                            break;
                        }
                        commandQueue.addLast(req);
                        System.out.println("curChunk = " + breakCounter);
                        var operationClass = Toolkit.Decode(request.input.dataClass, Class.class);
//                        for (var el : res) {
//                            var task = new TaskExecutor(operationClass, request.input.hashCode, el, request.uuid);
//                            var future = executor.submit(task);
//                            tasks.add(future);
//                        }

                        var task = new TaskExecutor(operationClass, req.hashCode, res.poll(), request.uuid);
                        var future = executor.submit(task);
                        tasks.add(future);
                        breakCounter++;
                        break;
                    }
                    case CloudExecutor.COMMAND_AND_DATA: {
                        breakCounter = 0;
                        if (!loaded) {
                            var classInjector = new ClassInjector(Thread.currentThread().getContextClassLoader());
                            classInjector.injectClasses(req.classCodes);
                            loaded = true;
                        }
                        var operationClass = Toolkit.Decode(req.operationClass, Class.class);
                        var dataClass = Toolkit.Decode(req.dataClass, Class.class);
                        var data = Toolkit.Decode(req.data, dataClass);
                        var task = new TaskExecutor(operationClass, req.hashCode, data, request.uuid);
                        var future = executor.submit(task);
                        tasks.add(future);
                        break;
                    }
                }
            }
            var doneTasks = tasks.stream().filter(Future::isDone).collect(Collectors.toList());
            var doneACK = dataACK.stream().filter(Future::isDone).collect(Collectors.toList());
            for(var doneAck : doneACK) {
                tasks.remove(doneAck);
                doneAck.get();
                var responseBuffer = Toolkit.Encode(new CloudNodeResponsePacket(null, null));
                outputStream.writeInt(responseBuffer.length);
                outputStream.write(responseBuffer);
                outputStream.flush();
                System.out.println("Task done: " + null);
            }
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
//            for (var curTask : tasks) {
//                res.add(curTask.get().answer);
//            }

            //curResult.answer = res.toArray();
            //curResult.uuid = request.uuid;
            //var doneTasks = tasks.stream().filter(Future::isDone).collect(Collectors.toList());
            //Thread.sleep(1000);
        }
    }


    private void loadData() {


    }

    private void dataAndCommand() {


    }


}
