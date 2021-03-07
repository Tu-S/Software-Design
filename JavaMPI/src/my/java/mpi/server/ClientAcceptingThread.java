package my.java.mpi.server;

import my.java.mpi.common.JavaMpiUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientAcceptingThread implements Runnable {

    private ConcurrentLinkedQueue<Channel> clients;
    private ServerSocket clientSocket;

    public ClientAcceptingThread(ConcurrentLinkedQueue<Channel> clients, ServerSocket clientSocket) {
        this.clients = clients;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        System.out.println("ClientThread up");
        while (!clientSocket.isClosed()) {
            try {
                var client = new Channel(clientSocket.accept());
                clients.add(client);

                System.out.println("Accepted task from: " + JavaMpiUtils.GetAddress(client.socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
