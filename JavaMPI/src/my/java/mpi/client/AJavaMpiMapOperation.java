package my.java.mpi.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class AJavaMpiMapOperation<TIn extends Serializable, TOut extends Serializable> extends AJavaMpiOperation<ArrayList<TIn>, ArrayList<TOut>> {

    public abstract TOut Convert(TIn input);

    @Override
    public ArrayList<TOut> Execute(ArrayList<TIn> input) {

        return input.stream().map(this::Convert).collect(Collectors.toCollection(ArrayList::new));
    }
}
