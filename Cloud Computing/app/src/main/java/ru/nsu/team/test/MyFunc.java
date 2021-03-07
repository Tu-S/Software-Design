package ru.nsu.team.test;

public class MyFunc {
    public static  <TIn, TOut> TOut staticMethod(TIn arg){
        System.out.println("static " + arg);
        Integer a = (Integer) arg;
        a++;
        return (TOut) Integer.toString(a);
    }

    public <TIn, TOut> TOut nonStaticMethod(TIn arg){
        System.out.println("non static" + arg);

        return (TOut) arg;
    }
}
