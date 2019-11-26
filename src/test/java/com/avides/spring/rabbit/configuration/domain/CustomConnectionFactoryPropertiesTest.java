package com.avides.spring.rabbit.configuration.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

public class CustomConnectionFactoryPropertiesTest implements DomainTestSupport
{
    @Test
    public void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteCustomConnectionFactoryProperties());
    }

    // addresses
    @Test
    public void testBeanValidationOnAddressesWithNull()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setAddresses(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "addresses");
    }

    @Test
    public void testBeanValidationOnAddressesWithEmpty()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setAddresses("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "addresses");
    }

    @Test
    public void testBeanValidationOnAddressesWithBlank()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setAddresses(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "addresses");
    }

    // username
    @Test
    public void testBeanValidationOnUsernameWithNull()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setUsername(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "username");
    }

    @Test
    public void testBeanValidationOnUsernameWithEmpty()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setUsername("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "username");
    }

    @Test
    public void testBeanValidationOnUsernameWithBlank()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setUsername(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "username");
    }

    // password
    @Test
    public void testBeanValidationOnPasswordWithNull()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setPassword(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "password");
    }

    @Test
    public void testBeanValidationOnPasswordWithEmpty()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setPassword("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "password");
    }

    @Test
    public void testBeanValidationOnPasswordWithBlank()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setPassword(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "password");
    }

    // virtualHost
    @Test
    public void testBeanValidationOnVirtualHostWithNull()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setVirtualHost(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "virtualHost");
    }

    @Test
    public void testBeanValidationOnVirtualHostWithEmpty()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setVirtualHost("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "virtualHost");
    }

    @Test
    public void testBeanValidationOnVirtualHostWithBlank()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setVirtualHost(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "virtualHost");
    }

    // beanName
    @Test
    public void testBeanValidationOnBeanNameWithNull()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithEmpty()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setBeanName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "beanName");
    }

    @Test
    public void testBeanValidationOnBeanNameWithBlank()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setBeanName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "beanName");
    }

    // apiPort
    @Test
    public void testBeanValidationOnApiPortWithLessThanOne()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setApiPort(0);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "apiPort");
    }

    @Test
    public void testBeanValidationOnApiPortWithMoreThan65535()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setApiPort(65536);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "apiPort");
    }

    /**
     * test default values
     */
    @Test
    public void testDefaultValueOnAddresses()
    {
        assertNull(new CustomConnectionFactoryProperties().getAddresses());
    }

    @Test
    public void testDefaultValueOnUsername()
    {
        assertNull(new CustomConnectionFactoryProperties().getUsername());
    }

    @Test
    public void testDefaultValueOnPassword()
    {
        assertNull(new CustomConnectionFactoryProperties().getPassword());
    }

    @Test
    public void testDefaultValueOnVirtualHost()
    {
        assertEquals("/", new CustomConnectionFactoryProperties().getVirtualHost());
    }

    @Test
    public void testDefaultValueOnBeanName()
    {
        assertNull(new CustomConnectionFactoryProperties().getBeanName());
    }

    @Test
    public void testDefaultValueOnApiPort()
    {
        assertEquals(15672, new CustomConnectionFactoryProperties().getApiPort());
    }
}
