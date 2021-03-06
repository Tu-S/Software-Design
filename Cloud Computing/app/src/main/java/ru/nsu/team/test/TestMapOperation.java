package ru.nsu.team.test;

import ru.nsu.team.client.CloudMapOperation;

import java.io.Serializable;

public class TestMapOperation extends CloudMapOperation<Integer,String> {


    @Override
    public String convert(Integer input) {
        return String.format("%dLOL%d", input, input);
    }
}
