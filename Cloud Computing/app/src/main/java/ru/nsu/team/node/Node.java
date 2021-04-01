package ru.nsu.team.node;

import ru.nsu.team.client.CloudExecutor;
import ru.nsu.team.packet.CloudNodePacket;
import ru.nsu.team.packet.CloudNodeResponsePacket;
import ru.nsu.team.packet.CloudNodeResultPacket;
import ru.nsu.team.tools.Toolkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Node {
    private static final String host = "localhost";
    private static final int port = 18226;

    public static void main(String[] args) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException {
        CloudNodeResultPacket curResult = new CloudNodeResultPacket();
        List<Object[]> res = new ArrayList<>();
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
                System.out.println(request.input.command);
                switch (request.input.command) {
                    case CloudExecutor.COMMAND: {
                        var operationClass = Toolkit.Decode(request.input.dataClass, Class.class);//Toolkit.Decode(request.input.operationClass, Class.class);
                        for (var el : res) {
                            var task = new TaskExecutor(operationClass, request.input.hashCode, el, request.uuid);
                            var future = executor.submit(task);
                            tasks.add(future);
                        }

                        //System.out.println("Task accepted: " + request.uuid.toString());
                        break;
                    }
                    case CloudExecutor.COMMAND_AND_DATA: {
                        var classInjector = new ClassInjector(Thread.currentThread().getContextClassLoader());
                        classInjector.injectClasses(request.input.classCodes);
                        var operationClass = Toolkit.Decode(request.input.operationClass, Class.class);
                        var dataClass = Toolkit.Decode(request.input.dataClass, Class.class);
                        var data = Toolkit.Decode(request.input.data, dataClass);
                        var task = new TaskExecutor(operationClass, request.input.hashCode, data, request.uuid);
                        var future = executor.submit(task);
                        tasks.add(future);
                        //System.out.println("Task accepted: " + request.uuid.toString());
                        break;
                    }
                    case CloudExecutor.COLLECT: {
                        List<Object> ans = new ArrayList<>();
                        for (var el : res) {
                            for(var e : el){
                                ans.add(e);
                            }
                        }
                            var responseBuffer = Toolkit.Encode(ans.toArray());
                            outputStream.writeInt(responseBuffer.length);
                            outputStream.write(responseBuffer);
                            outputStream.flush();
                            System.out.println("Task done: " + curResult.uuid.toString());



                        break;
                    }

                }

            }
            for (var curTask : tasks) {
                res.add(curTask.get().answer);
            }
            tasks.clear();
            //curResult.answer = res.toArray();
            //curResult.uuid = request.uuid;
            //var doneTasks = tasks.stream().filter(Future::isDone).collect(Collectors.toList());
            Thread.sleep(1000);
        }
    }


    private void loadData() {


    }

    private void dataAndCommand() {


    }


}
