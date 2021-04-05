package ru.nsu.team.packet;

import java.io.Serializable;
import java.util.UUID;

public class CloudNodeResultPacket implements Serializable {
    public UUID uuid;
    public Object[] answer;

    public CloudNodeResultPacket() {
    }

    public CloudNodeResultPacket(final UUID uuid, final Object[] answer) {
        this.uuid = uuid;
        this.answer = answer;
    }
}
