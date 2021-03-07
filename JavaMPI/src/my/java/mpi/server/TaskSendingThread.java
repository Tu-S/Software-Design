package my.java.mpi.server;

import my.java.mpi.common.JavaMpiUtils;
import my.java.mpi.common.KeyValuePair;
import my.java.mpi.server.dtos.JavaMpiDto;
import my.java.mpi.server.dtos.JavaMpiNodeDto;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskSendingThread implements Runnable {
    private ConcurrentLinkedQueue<Channel> nodes;
    private ConcurrentLinkedQueue<Channel> clients;
    private ConcurrentLinkedQueue<ChannelPair> tasks;

    public TaskSendingThread(ConcurrentLinkedQueue<Channel> nodes, ConcurrentLinkedQueue<Channel> clients, ConcurrentLinkedQueue<ChannelPair> tasks) {
        this.nodes = nodes;
        this.clients = clients;
        this.tasks = tasks;
    }

    @Override
    public void run() {
        System.out.println("TaskThread up");
        while (true) {
            try {
                if (clients.isEmpty()) {
                    Thread.sleep(1000);
                    continue;
                }
                var client = clients.poll();
                while (nodes.isEmpty()) {
                    Thread.sleep(1000);
                }
                var node = nodes.poll();
                nodes.add(node);
                while (client.input.available() < 4) {
                    Thread.sleep(1000);
                }
                var requestArray = new byte[client.input.readInt()];
                client.input.readFully(requestArray);
                var request = JavaMpiUtils.Decode(requestArray, JavaMpiDto.class);
                var executionRequest = new JavaMpiNodeDto();
                executionRequest.id = UUID.randomUUID();
                executionRequest.input = request;
                requestArray = JavaMpiUtils.Encode(executionRequest);
                node.output.writeInt(requestArray.length);
                node.output.write(requestArray);
                node.output.flush();
                tasks.add(new ChannelPair(node, client));
                System.out.println("New task");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
