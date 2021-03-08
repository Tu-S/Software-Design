package my.java.mpi.node;

import my.java.mpi.annoatation.Remote;
import my.java.mpi.client.AJavaMpiOperation;
import my.java.mpi.common.JavaMpiUtils;
import my.java.mpi.server.dtos.JavaMpiNodeResponseDto;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Callable;

public class TaskExecutor <TIn extends Serializable, TOut extends Serializable, TOperation /*extends AJavaMpiOperation<TIn, TOut>*/> implements Callable<JavaMpiNodeResponseDto> {

    private Class<TOperation> operationClass;
    private TIn data;
    private UUID id;

    public TaskExecutor(Class<TOperation> operationClass, TIn data, UUID id) {
        this.operationClass = operationClass;
        this.data = data;
        this.id = id;
    }


    @Override
    public JavaMpiNodeResponseDto call() throws Exception {
        var operation = (TOperation) operationClass.getDeclaredConstructor().newInstance();// newInstance();
        Method ex = null;
        for(Method m : operationClass.getDeclaredMethods()){
            if(m.isAnnotationPresent(Remote.class)){
                ex = m;
                break;
            }
        }
        System.out.println("method name " + ex.getName());
        var ans = new JavaMpiNodeResponseDto();
        ans.id = id;
        ans.answer = JavaMpiUtils.testEncode(ex.invoke(operation,data)); //JavaMpiUtils.Encode(ex.invoke(operation,data));//
        return ans;
    }
}
