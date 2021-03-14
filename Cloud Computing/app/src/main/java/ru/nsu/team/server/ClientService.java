package ru.nsu.team.server;

import ru.nsu.team.tools.Toolkit;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientService implements Runnable {
  private ConcurrentLinkedQueue<DataChannel> clients;
  private ServerSocket clientSocket;

  public ClientService(final ConcurrentLinkedQueue<DataChannel> clients, final ServerSocket clientSocket) {
    this.clients = clients;
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    System.out.println("ClientService is running...");
    while (!clientSocket.isClosed()) {
      try {
        var client = new DataChannel(clientSocket.accept());
        clients.add(client);
        System.out.println("Accepted task from: " + client.getSocket().getInetAddress().getHostName());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
