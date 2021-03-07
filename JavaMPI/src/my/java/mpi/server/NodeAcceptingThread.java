package my.java.mpi.server;

import my.java.mpi.common.JavaMpiUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NodeAcceptingThread implements Runnable {

    private ConcurrentLinkedQueue<Channel> nodes;
    private ServerSocket nodeSocket;

    public NodeAcceptingThread(ConcurrentLinkedQueue<Channel> nodes, ServerSocket nodeSocket) {
        this.nodes = nodes;
        this.nodeSocket = nodeSocket;
    }

    @Override
    public void run() {
        System.out.println("NodeThread up");
        while (!nodeSocket.isClosed()) {
            Channel node = null;
            try {
                node = new Channel(nodeSocket.accept());
                nodes.add(node);
                System.out.println("Accepted node: " + JavaMpiUtils.GetAddress(node.socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
