package ru.nsu.team.client;

import ru.nsu.team.tools.lambda.SerializableFunction;

import java.util.Collection;
import java.util.concurrent.Callable;

public class LocalCallable<T, R> implements Callable<Object[]> {

    private final T[] data;
    private final SerializableFunction<? super T[], ? extends R> mapper;
    private final int commandType;

    public LocalCallable(int commandType,Collection<T> data, SerializableFunction<? super T[], ? extends R> mapper) {
        this.commandType = commandType;
        this.data = (T[]) data.toArray();
        this.mapper = mapper;
    }

    @Override
    public Object[] call() throws Exception {
        return CloudExecutor.testDataCommandExecute(commandType,data, mapper);
    }
}

