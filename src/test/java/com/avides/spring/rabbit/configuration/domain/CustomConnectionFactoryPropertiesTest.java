package com.avides.spring.rabbit.configuration.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.avides.spring.rabbit.utils.BeanValidationTestSupport;
import com.avides.spring.rabbit.utils.DomainTestSupport;

class CustomConnectionFactoryPropertiesTest implements DomainTestSupport
{
    @Test
    void testBeanValidation()
    {
        BeanValidationTestSupport.expectNoError(getCompleteCustomConnectionFactoryProperties());
    }

    // addresses
    @Test
    void testBeanValidationOnAddressesWithNull()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setAddresses(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "addresses");
    }

    @Test
    void testBeanValidationOnAddressesWithEmpty()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setAddresses("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "addresses");
    }

    @Test
    void testBeanValidationOnAddressesWithBlank()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setAddresses(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "addresses");
    }

    // username
    @Test
    void testBeanValidationOnUsernameWithNull()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setUsername(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "username");
    }

    @Test
    void testBeanValidationOnUsernameWithEmpty()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setUsername("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "username");
    }

    @Test
    void testBeanValidationOnUsernameWithBlank()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setUsername(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "username");
    }

    // password
    @Test
    void testBeanValidationOnPasswordWithNull()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setPassword(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "password");
    }

    @Test
    void testBeanValidationOnPasswordWithEmpty()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setPassword("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "password");
    }

    @Test
    void testBeanValidationOnPasswordWithBlank()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setPassword(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "password");
    }

    // virtualHost
    @Test
    void testBeanValidationOnVirtualHostWithNull()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setVirtualHost(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "virtualHost");
    }

    @Test
    void testBeanValidationOnVirtualHostWithEmpty()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setVirtualHost("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "virtualHost");
    }

    @Test
    void testBeanValidationOnVirtualHostWithBlank()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setVirtualHost(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "virtualHost");
    }

    // beanName
    @Test
    void testBeanValidationOnBeanNameWithNull()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setBeanName(null);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "beanName");
    }

    @Test
    void testBeanValidationOnBeanNameWithEmpty()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setBeanName("");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "beanName");
    }

    @Test
    void testBeanValidationOnBeanNameWithBlank()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setBeanName(" ");
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "beanName");
    }

    // apiPort
    @Test
    void testBeanValidationOnApiPortWithLessThanOne()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setApiPort(0);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "apiPort");
    }

    @Test
    void testBeanValidationOnApiPortWithMoreThan65535()
    {
        CustomConnectionFactoryProperties customConnectionFactoryProperties = getCompleteCustomConnectionFactoryProperties();
        customConnectionFactoryProperties.setApiPort(65536);
        BeanValidationTestSupport.expectErrorOnlyOnProperty(customConnectionFactoryProperties, "apiPort");
    }

    /**
     * test default values
     */
    @Test
    void testDefaultValueOnAddresses()
    {
        assertNull(new CustomConnectionFactoryProperties().getAddresses());
    }

    @Test
    void testDefaultValueOnUsername()
    {
        assertNull(new CustomConnectionFactoryProperties().getUsername());
    }

    @Test
    void testDefaultValueOnPassword()
    {
        assertNull(new CustomConnectionFactoryProperties().getPassword());
    }

    @Test
    void testDefaultValueOnVirtualHost()
    {
        assertEquals("/", new CustomConnectionFactoryProperties().getVirtualHost());
    }

    @Test
    void testDefaultValueOnBeanName()
    {
        assertNull(new CustomConnectionFactoryProperties().getBeanName());
    }

    @Test
    void testDefaultValueOnApiPort()
    {
        assertEquals(15672, new CustomConnectionFactoryProperties().getApiPort());
    }
}
