package com.exercise.book;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor

public class LombokTest {
    private String name;
    private int age;

    public static void main(String[] args) {
        LombokTest test = new LombokTest();
        test.setName("Lombok Example");
        test.setAge(25);

        System.out.println("Name: " + test.getName());
        System.out.println("Age: " + test.getAge());
    }
}
