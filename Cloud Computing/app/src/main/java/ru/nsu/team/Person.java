package ru.nsu.team;

import java.io.Serializable;
import java.util.Objects;

public class Person implements Serializable {
  public Integer age;
  public String name;

  public Person(Integer age, String name) {
    this.age = age;
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Person person = (Person) o;
    return Objects.equals(age, person.age) && Objects.equals(name, person.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(age, name);
  }
}
