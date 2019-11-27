package com.avides.spring.rabbit.test.support;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class OtherTestClass
{
    private float otherFloatProperty;

    private String otherStringProperty;

    private Integer otherIntegerProperty;

    private OtherSubTestClass otherSubTestClass;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    static class OtherSubTestClass
    {
        private int otherIntProperty;

        private Double otherDoubleProperty;
    }

    public static OtherTestClass buildBase()
    {
        OtherTestClass testClass = new OtherTestClass();
        testClass.setOtherFloatProperty(2);
        return testClass;
    }

    public static OtherTestClass buildComplete()
    {
        OtherTestClass testClass = buildBase();
        testClass.setOtherStringProperty("string");
        testClass.setOtherIntegerProperty(Integer.valueOf(3));
        testClass.setOtherSubTestClass(new OtherSubTestClass(4, Double.valueOf(5)));
        return testClass;
    }
}
