package my.java.mpi.server;

import my.java.mpi.common.JavaMpiUtils;
import my.java.mpi.server.dtos.JavaMpiNodeResponseDto;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class TaskResultsReceivingThread implements Runnable {
    private ConcurrentLinkedQueue<ChannelPair> tasks;

    public TaskResultsReceivingThread(ConcurrentLinkedQueue<ChannelPair> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run() {
        System.out.println("ResultThread up");
        while (true) {
            try {
                var readyResults = tasks.stream().filter(t -> {
                    try {
                        return t.nodeChannel.input.available() > 0;
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
                for (var res : readyResults) {
                    var responseBuffer = new byte[res.nodeChannel.input.readInt()];
                    res.nodeChannel.input.readFully(responseBuffer);
                    var ans = JavaMpiUtils.<JavaMpiNodeResponseDto>Decode(responseBuffer, JavaMpiNodeResponseDto.class);
                    res.clientChannel.output.writeInt(ans.answer.length);
                    res.clientChannel.output.write(ans.answer);
                    res.clientChannel.output.flush();
                    res.clientChannel.input.close();
                    res.clientChannel.output.close();
                    res.clientChannel.socket.close();
                    System.out.println("Task ended");
                }
            } catch (InterruptedException e) {
                break;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
