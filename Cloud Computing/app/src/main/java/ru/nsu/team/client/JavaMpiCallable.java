package ru.nsu.team.client;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JavaMpiCallable<T,R> implements Callable<List<Object[]>> {

    private List<LocalCallable<T,R>> subTasks;
    private List<Future<Object[]>> awaitedSubTasks;

    public JavaMpiCallable(List<LocalCallable<T,R>> subTasks) {
        this.subTasks = subTasks;
    }

    public double progress() {
        if (awaitedSubTasks == null)
            throw new RuntimeException("Task not started");
        return 1.0 * awaitedSubTasks.stream().filter(Future::isDone).count() / subTasks.size();
    }

    @Override
    public List<Object[]> call() throws Exception {
        var executor = Executors.newFixedThreadPool(subTasks.size());
        awaitedSubTasks = new LinkedList<>();
        for (var task : subTasks) {
            awaitedSubTasks.add(executor.submit(task));
        }
        var res = new LinkedList<Object[]>();
        for (var task : awaitedSubTasks) {
            var val = task.get();
            res.addAll(Collections.singleton(task.get()));
        }
        executor.shutdown();
        return res;
    }
}
