package com.avides.spring.rabbit.test.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TestClass
{
    private float floatProperty;

    private String stringProperty;

    private Integer integerProperty;

    private SubTestClass subTestClass;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    static class SubTestClass
    {
        private int intProperty;

        private Double doubleProperty;
    }

    public static TestClass buildBase()
    {
        TestClass testClass = new TestClass();
        testClass.setFloatProperty(2);
        return testClass;
    }

    public static TestClass buildComplete()
    {
        TestClass testClass = buildBase();
        testClass.setStringProperty("string");
        testClass.setIntegerProperty(Integer.valueOf(3));
        testClass.setSubTestClass(new SubTestClass(4, Double.valueOf(5)));
        return testClass;
    }
}
