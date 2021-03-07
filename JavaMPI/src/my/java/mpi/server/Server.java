package my.java.mpi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) throws IOException {
        var clientSocket = new ServerSocket(18228);
        var nodeSocket = new ServerSocket(18226);
        var nodes = new ConcurrentLinkedQueue<Channel>();
        var clients = new ConcurrentLinkedQueue<Channel>();
        var tasks = new ConcurrentLinkedQueue<ChannelPair>();
        var executor = Executors.newFixedThreadPool(3);
        executor.submit(new ClientAcceptingThread(clients, clientSocket));
        executor.submit(new NodeAcceptingThread(nodes, nodeSocket));
        executor.submit(new TaskResultsReceivingThread(tasks));
        var payloadForMainThread = new TaskSendingThread(nodes, clients, tasks);
        payloadForMainThread.run();
    }

}
