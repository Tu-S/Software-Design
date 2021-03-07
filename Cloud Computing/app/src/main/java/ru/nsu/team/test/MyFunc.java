package ru.nsu.team.test;

import ru.nsu.team.annotation.Remote;

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

    @Remote
    public static String[] exec(Integer[] input){
        var ans = new String[input.length];
        for (int i = 0; i < input.length; i++) {
            ans[i] = String.format("%s%s", input[i].toString(), input[i].toString());
        }
        return ans;

    }
}
