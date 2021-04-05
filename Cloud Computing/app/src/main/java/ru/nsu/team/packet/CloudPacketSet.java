package ru.nsu.team.packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CloudPacketSet implements Serializable {

    public List<CloudPacket> packets = new ArrayList<>();
    public int command;
    public CloudPacketSet(int command){
        this.command = command;
    }



}
