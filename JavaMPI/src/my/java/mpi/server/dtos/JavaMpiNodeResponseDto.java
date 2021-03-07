package my.java.mpi.server.dtos;

import java.io.Serializable;
import java.util.UUID;

public class JavaMpiNodeResponseDto implements Serializable {
    public UUID id;
    public byte[] answer;
}
