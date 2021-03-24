package ru.nsu.team.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

public class Server {
  public static void main(String[] args) throws IOException {
    var clientSocket = new ServerSocket(18228);
    var nodeSocket = new ServerSocket(18226);
    var nodes = new ConcurrentLinkedQueue<DataChannel>();
    var clients = new ConcurrentLinkedQueue<DataChannel>();
    var tasks = new ConcurrentLinkedQueue<NCDataChannel>();
    var executor = Executors.newFixedThreadPool(3);
    executor.submit(new ClientService(clients, clientSocket));
    executor.submit(new NodeService(nodes, nodeSocket));
    executor.submit(new TaskResultsService(tasks));
    var taskSendingService = new TaskSendingService(nodes, clients, tasks);
    taskSendingService.run();
  }
}
