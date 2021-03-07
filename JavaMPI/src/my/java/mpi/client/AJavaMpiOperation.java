package my.java.mpi.client;

import java.io.Serializable;

public abstract class AJavaMpiOperation<TIn extends Serializable, TOut extends Serializable> {
    public abstract TOut Execute(TIn input);
}
