package ru.nsu.team.client;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CloudCallable<T> implements Callable<List<T>> {

    private List<? extends Callable<? extends List<T>>> subTasks;
    private List<Future<? extends List<T>>> awaitedSubTasks;

    public CloudCallable(List<? extends Callable<? extends List<T>>> subTasks) {
        this.subTasks = subTasks;
    }

    public double progress() {
        if (awaitedSubTasks == null)
            throw new RuntimeException("Task not started");
        return 1.0 * awaitedSubTasks.stream().filter(Future::isDone).count() / subTasks.size();
    }

    @Override
    public List<T> call() throws Exception {
        var executor = Executors.newFixedThreadPool(subTasks.size());
        awaitedSubTasks = new LinkedList<>();
        for (var task : subTasks) {
            awaitedSubTasks.add(executor.submit(task));
        }
        var res = new LinkedList<T>();
        for (var task : awaitedSubTasks) {
            res.addAll(task.get());
        }
        executor.shutdown();
        return res;
    }
}
