package ru.nsu.team.client;

import ru.nsu.team.tools.lambda.SerializableFunction;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class LocalCallable<T, R> implements Callable<Object[]> {

    private final T[] data;
    private final List<SerializableFunction<? super T[], ? extends R>> mappers;
    private final int commandType;

    public LocalCallable(int commandType,Collection<T> data, List<SerializableFunction<? super T[], ? extends R>> mappers) {
        this.commandType = commandType;
        this.data = (T[]) data.toArray();
        this.mappers = mappers;
    }

    @Override
    public Object[] call() throws Exception {
        return CloudExecutor.testDataCommandExecute(commandType,data, mappers);
    }
}

