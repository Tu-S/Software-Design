package ru.nsu.team.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DataChannel {
  private Socket socket;
  private DataInputStream inputStream;
  private DataOutputStream outputStream;

  public DataChannel(final Socket socket) throws IOException {
    this.socket = socket;
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
  }

  public Socket getSocket() {
    return socket;
  }

  public DataInputStream getInputStream() {
    return inputStream;
  }

  public DataOutputStream getOutputStream() {
    return outputStream;
  }
}
