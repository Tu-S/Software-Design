package ru.nsu.team.packet;

import java.io.Serializable;
import java.util.UUID;

public class CloudNodePacket implements Serializable {
    public UUID uuid;
    public CloudPacketSet input;

    public CloudNodePacket() {
    }

    public CloudNodePacket(final UUID uuid, final CloudPacketSet input) {
        this.uuid = uuid;
        this.input = input;
    }
}
