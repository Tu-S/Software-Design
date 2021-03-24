package ru.nsu.team.server;

import ru.nsu.team.packet.CloudNodeResponsePacket;
import ru.nsu.team.tools.Toolkit;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class TaskResultsService implements Runnable {
  private final ConcurrentLinkedQueue<NCDataChannel> tasks;

  public TaskResultsService(final ConcurrentLinkedQueue<NCDataChannel> tasks) {
    this.tasks = tasks;
  }

  @Override
  public void run() {
    System.out.println("TaskResultsService is running...");
    while (true) {
      try {
        var readyResults = tasks.stream().filter(t -> {
          try {
            return t.getNodeChannel().getInputStream().available() > 0;
          } catch (IOException e) {
            e.printStackTrace();
            return false;
          }
        }).collect(Collectors.toList());
        if (readyResults.size() == 0) {
          Thread.sleep(1000);
          continue;
        }
        tasks.removeAll(readyResults);
        for (var result : readyResults) {
          var responseBuffer = new byte[result.getNodeChannel().getInputStream().readInt()];
          result.getNodeChannel().getInputStream().readFully(responseBuffer);
          var answer = Toolkit.Decode(responseBuffer, CloudNodeResponsePacket.class);
          result.getClientChannel().getOutputStream().writeInt(answer.answer.length);
          result.getClientChannel().getOutputStream().write(answer.answer);
          result.getClientChannel().getOutputStream().flush();
          result.getClientChannel().getInputStream().close();
          result.getClientChannel().getOutputStream().close();
          result.getClientChannel().getSocket().close();
          System.out.println("Task finished.");
        }
      } catch (InterruptedException e) {
        break;
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }
}
