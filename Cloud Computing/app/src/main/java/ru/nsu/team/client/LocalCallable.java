package ru.nsu.team.client;

import ru.nsu.team.tools.lambda.SerializableFunction;

import java.util.Collection;
import java.util.concurrent.Callable;

public class LocalCallable<T, R> implements Callable<Object[]> {

    private Object[] data;
    private SerializableFunction<? super T, ? extends R> mapper;

    public LocalCallable(Collection<T> data, SerializableFunction<? super T, ? extends R> mapper) {
        this.data = data.toArray(Object[]::new);
        this.mapper = mapper;
    }

    @Override
    public Object[] call() throws Exception {
        return CloudExecutor.testExecute(data, mapper);// <TIn, TOperation, TOut>Execute(data, inClass, operationClass, outClass);
    }
}

