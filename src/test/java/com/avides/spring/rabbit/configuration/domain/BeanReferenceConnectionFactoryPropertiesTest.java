package com.avides.spring.rabbit.configuration.domain;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

class BeanReferenceConnectionFactoryPropertiesTest implements DomainTestSupport
{
    @Test
    void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteBeanReferenceConnectionFactoryProperties());
    }

    // beanName
    @Test
    void testBeanValidationOnBeanNameWithNull()
    {
        BeanReferenceConnectionFactoryProperties beanReferenceConnectionFactoryProperties = getCompleteBeanReferenceConnectionFactoryProperties();
        beanReferenceConnectionFactoryProperties.setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(beanReferenceConnectionFactoryProperties, "beanName");
    }

    @Test
    void testBeanValidationOnBeanNameWithEmpty()
    {
        BeanReferenceConnectionFactoryProperties beanReferenceConnectionFactoryProperties = getCompleteBeanReferenceConnectionFactoryProperties();
        beanReferenceConnectionFactoryProperties.setBeanName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(beanReferenceConnectionFactoryProperties, "beanName");
    }

    @Test
    void testBeanValidationOnBeanNameWithBlank()
    {
        BeanReferenceConnectionFactoryProperties beanReferenceConnectionFactoryProperties = getCompleteBeanReferenceConnectionFactoryProperties();
        beanReferenceConnectionFactoryProperties.setBeanName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(beanReferenceConnectionFactoryProperties, "beanName");
    }

    /**
     * test default values
     */
    @Test
    void testDefaultValueOnBeanName()
    {
        assertNull(new BeanReferenceConnectionFactoryProperties().getBeanName());
    }
}
