package ru.nsu.team.test;

import java.util.function.Function;

public class MyFunc {
    public static  <Tin> Tin staticMethod(Tin arg){
        System.out.println("static " + arg);
        return arg;
    }

    public <Tin> Tin nonStaticMethod(Tin arg){
        System.out.println("non static" + arg);
        return arg;
    }
}
