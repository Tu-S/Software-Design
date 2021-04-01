package ru.nsu.team.packet;

import java.nio.file.ClosedDirectoryStreamException;
import java.util.UUID;

public class CloudNodeResultPacket {
    public UUID uuid;
    public Object[] answer;

    public CloudNodeResultPacket() {
    }

    public CloudNodeResultPacket(final UUID uuid, final Object[] answer) {
        this.uuid = uuid;
        this.answer = answer;
    }
}
