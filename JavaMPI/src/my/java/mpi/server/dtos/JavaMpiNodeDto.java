package my.java.mpi.server.dtos;

import java.io.Serializable;
import java.util.UUID;

public class JavaMpiNodeDto  implements Serializable {
    public UUID id;
    public JavaMpiDto input;

    public JavaMpiNodeDto() {
    }

    public JavaMpiNodeDto(UUID id, JavaMpiDto input) {
        this.id = id;
        this.input = input;
    }
}
