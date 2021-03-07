package my.java.mpi.server;

public class ChannelPair {
    public Channel nodeChannel;
    public Channel clientChannel;

    public ChannelPair(Channel nodeChannel, Channel clientChannel) {
        this.nodeChannel = nodeChannel;
        this.clientChannel = clientChannel;
    }
}
