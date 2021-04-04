package ru.nsu.team.node;

import java.util.Collection;
import java.util.concurrent.Callable;

public class ACK implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        return 666;
    }
}
