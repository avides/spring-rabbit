package com.avides.spring.rabbit.configuration.domain;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

public class BeanReferenceConnectionFactoryPropertiesTest implements DomainTestSupport
{
    @Test
    public void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteBeanReferenceConnectionFactoryProperties());
    }

    // beanName
    @Test
    public void testBeanValidationOnBeanNameWithNull()
    {
        BeanReferenceConnectionFactoryProperties beanReferenceConnectionFactoryProperties = getCompleteBeanReferenceConnectionFactoryProperties();
        beanReferenceConnectionFactoryProperties.setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(beanReferenceConnectionFactoryProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithEmpty()
    {
        BeanReferenceConnectionFactoryProperties beanReferenceConnectionFactoryProperties = getCompleteBeanReferenceConnectionFactoryProperties();
        beanReferenceConnectionFactoryProperties.setBeanName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(beanReferenceConnectionFactoryProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithBlank()
    {
        BeanReferenceConnectionFactoryProperties beanReferenceConnectionFactoryProperties = getCompleteBeanReferenceConnectionFactoryProperties();
        beanReferenceConnectionFactoryProperties.setBeanName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(beanReferenceConnectionFactoryProperties, "beanName");
    }

    /**
     * test default values
     */
    @Test
    public void testDefaultValueOnBeanName()
    {
        assertNull(new BeanReferenceConnectionFactoryProperties().getBeanName());
    }
}
