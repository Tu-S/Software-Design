package ru.nsu.team.packet;

import java.io.Serializable;
import java.util.UUID;

public class CloudNodeResponsePacket implements Serializable {
    public UUID uuid;
    public byte[] answer;

    public CloudNodeResponsePacket(final UUID uuid, final byte[] answer) {
        this.uuid = uuid;
        this.answer = answer;
    }
}
