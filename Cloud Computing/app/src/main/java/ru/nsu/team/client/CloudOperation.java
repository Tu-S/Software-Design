package ru.nsu.team.client;

import java.io.Serializable;


public abstract class CloudOperation<TIn extends Serializable, TOut extends Serializable> {
    public abstract TOut Execute(TIn input);
}
