package ru.nsu.team.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class CloudMapOperation<TIn extends Serializable, TOut extends Serializable> extends CloudOperation<ArrayList<TIn>, ArrayList<TOut>> {

    public abstract TOut convert(TIn input);

    @Override
    public ArrayList<TOut> execute(ArrayList<TIn> input) {
        return input.stream().map(this::convert).collect(Collectors.toCollection(ArrayList::new));
    }
}
