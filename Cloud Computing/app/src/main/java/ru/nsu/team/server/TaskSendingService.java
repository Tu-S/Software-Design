package ru.nsu.team.server;

import ru.nsu.team.packet.CloudNodePacket;
import ru.nsu.team.packet.CloudPacket;
import ru.nsu.team.tools.Toolkit;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskSendingService implements Runnable {
  private ConcurrentLinkedQueue<DataChannel> nodes;
  private ConcurrentLinkedQueue<DataChannel> clients;
  private ConcurrentLinkedQueue<NCDataChannel> tasks;

  public TaskSendingService(final ConcurrentLinkedQueue<DataChannel> nodes,
                            final ConcurrentLinkedQueue<DataChannel> clients,
                            final ConcurrentLinkedQueue<NCDataChannel> tasks) {
    this.nodes = nodes;
    this.clients = clients;
    this.tasks = tasks;
  }

  @Override
  public void run() {
    System.out.println("TaskSendingService is running...");
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
        while (client.getInputStream().available() < 4) {
          Thread.sleep(1000);
        }
        var requestArr = new byte[client.getInputStream().readInt()];
        client.getInputStream().readFully(requestArr);
        var request = Toolkit.Decode(requestArr, CloudPacket.class);
        var executionRequest = new CloudNodePacket(UUID.randomUUID(), request);
        requestArr = Toolkit.Encode(executionRequest);
        node.getOutputStream().writeInt(requestArr.length);
        node.getOutputStream().write(requestArr);
        node.getOutputStream().flush();
        tasks.add((new NCDataChannel(node, client)));
        System.out.println("New task added.");
      } catch (InterruptedException e) {
        break;
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }
}
