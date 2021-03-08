package my.java.mpi.test;

import java.io.Serializable;

public class Person implements Serializable {
    public Integer age;
    public String name;
    public Person(Integer age, String name){
        this.age = age;
        this.name = name;

    }
}
