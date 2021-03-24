package ru.nsu.team.server;

public class NCDataChannel {
    private DataChannel nodeChannel;
    private DataChannel clientChannel;

    public NCDataChannel(final DataChannel nodeChannel, final DataChannel clientChannel) {
        this.nodeChannel = nodeChannel;
        this.clientChannel = clientChannel;
    }

    public DataChannel getNodeChannel() {
        return nodeChannel;
    }

    public DataChannel getClientChannel() {
        return clientChannel;
    }
}
