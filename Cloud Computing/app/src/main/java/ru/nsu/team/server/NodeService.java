package ru.nsu.team.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NodeService implements Runnable {
    private final ConcurrentLinkedQueue<DataChannel> nodes;
    private final ServerSocket nodeSocket;

    public NodeService(final ConcurrentLinkedQueue<DataChannel> nodes, final ServerSocket nodeSocket) {
        this.nodes = nodes;
        this.nodeSocket = nodeSocket;
    }

    @Override
    public void run() {
        System.out.println("NodeService is running...");
        while (!nodeSocket.isClosed()) {
            try {
                var node = new DataChannel(nodeSocket.accept());
                nodes.add(node);
                System.out.println("Accepted node: " + node.getSocket().getInetAddress().getHostName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
