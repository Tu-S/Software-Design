package ru.nsu.team.test;

import ru.nsu.team.annotation.Remote;
import ru.nsu.team.client.CloudExecutor;

import javax.xml.stream.events.Comment;
import java.lang.reflect.Method;

public class TestAnnotation {


    public static void testAnnotation(){
        MyFunc func = new MyFunc();

        for(Method m : func.getClass().getDeclaredMethods()){
            if(m.isAnnotationPresent(Remote.class)){
               System.out.println(m.getName());
               for(Class<?> c :m.getParameterTypes() ){
                   System.out.println("in element type =" +c.getComponentType());
               }
                Class<?> outClass = m.getReturnType();

               System.out.println("out Element type = " + outClass.getComponentType());
                System.out.println("array type = " + outClass.arrayType().getSimpleName());
               break;
            }


        }

    }

    public void checkClass(){


    }
}
