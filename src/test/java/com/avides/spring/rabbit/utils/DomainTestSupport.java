package com.avides.spring.rabbit.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import com.avides.spring.rabbit.configuration.SpringRabbitAutoConfiguration;
import com.avides.spring.rabbit.configuration.domain.BeanReferenceConnectionFactoryProperties;
import com.avides.spring.rabbit.configuration.domain.CustomConnectionFactoryProperties;
import com.avides.spring.rabbit.configuration.domain.ExchangeProperties;
import com.avides.spring.rabbit.configuration.domain.ExchangeProperties.ExchangeType;
import com.avides.spring.rabbit.configuration.domain.ListenerProperties;
import com.avides.spring.rabbit.configuration.domain.MessageConverterProperties;
import com.avides.spring.rabbit.configuration.domain.QueueProperties;
import com.avides.spring.rabbit.configuration.domain.RabbitAdminProperties;
import com.avides.spring.rabbit.configuration.domain.RabbitTemplateProperties;
import com.rabbitmq.http.client.domain.QueueInfo;

public interface DomainTestSupport
{
    default SpringRabbitAutoConfiguration getCompleteSpringRabbitAutoConfiguration()
    {
        SpringRabbitAutoConfiguration springRabbitAutoConfiguration = new SpringRabbitAutoConfiguration();
        springRabbitAutoConfiguration.setQueues(Collections.singletonList(getCompleteQueueProperties()));
        springRabbitAutoConfiguration.setOutbounds(Collections.singletonList(getCompleteRabbitTemplateProperties()));
        springRabbitAutoConfiguration.setConnections(Collections.singletonList(getCompleteCustomConnectionFactoryProperties()));
        springRabbitAutoConfiguration.setExchange(getCompleteExchangeProperties());
        springRabbitAutoConfiguration.setMessageConverter(getCompleteMessageConverterProperties());
        springRabbitAutoConfiguration.setMaxConcurrentConsumers(Integer.valueOf(4));
        springRabbitAutoConfiguration.setApiPort(6699);
        return springRabbitAutoConfiguration;
    }

    default RabbitTemplateProperties getCompleteRabbitTemplateProperties()
    {
        RabbitTemplateProperties properties = new RabbitTemplateProperties();
        properties.setExchange(getCompleteExchangeProperties());
        properties.setRoutingkey("testRoutingKey");
        properties.setBeanName("testRabbitTemplate");
        properties.setMessageConverter(getCompleteMessageConverterProperties());
        properties.setConnectionFactory(getCompleteBeanReferenceConnectionFactoryProperties());
        return properties;
    }

    default QueueInfo getQueueInfo()
    {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "");
        arguments.put("x-dead-letter-routing-key", "com.avides.spring.rabbit.queue.zero.dlx");
        arguments.put("x-max-length", Long.valueOf(100));

        QueueInfo queueInfo = new QueueInfo();
        queueInfo.setName("com.avides.spring.rabbit.queue.zero");
        queueInfo.setArguments(arguments);
        queueInfo.setDurable(true);
        queueInfo.setExclusive(false);
        queueInfo.setNode("rabbit@localhost");
        return queueInfo;
    }

    default QueueInfo getQueueInfoWithExclusive()
    {
        QueueInfo queueInfo = getQueueInfo();
        queueInfo.setExclusive(true);
        return queueInfo;
    }

    default QueueProperties getCompleteQueueProperties()
    {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "");
        arguments.put("x-dead-letter-routing-key", "testQueueName.dlx");
        arguments.put("x-max-length", Long.valueOf(100));

        QueueProperties queueProperties = new QueueProperties();
        queueProperties.setCreationEnabled(true);
        queueProperties.setRoutingkey("product");
        queueProperties.setName("testQueueName");
        queueProperties.setLimit(100);
        queueProperties.setDurable(true);
        queueProperties.setExclusive(false);
        queueProperties.setArguments(arguments);
        queueProperties.setExchange(getCompleteExchangeProperties());
        queueProperties.setRabbitAdmin(getCompleteRabbitAdminProperties());
        queueProperties.setListener(getCompleteListenerProperties());
        return queueProperties;
    }

    default ListenerProperties getCompleteListenerProperties()
    {
        ListenerProperties listenerProperties = new ListenerProperties();
        listenerProperties.setBeanName("testListener");
        listenerProperties.setCreationEnabled(true);
        listenerProperties.setMessageConverter(getCompleteMessageConverterProperties());
        listenerProperties.setMaxConcurrentConsumers(Integer.valueOf(2));
        return listenerProperties;
    }

    default RabbitAdminProperties getCompleteRabbitAdminProperties()
    {
        RabbitAdminProperties rabbitAdminProperties = new RabbitAdminProperties();
        rabbitAdminProperties.setBeanName("rabbitAdminBeanName");
        rabbitAdminProperties.setConnectionFactory(getCompleteBeanReferenceConnectionFactoryProperties());
        return rabbitAdminProperties;
    }

    default CustomConnectionFactoryProperties getCompleteCustomConnectionFactoryProperties()
    {
        CustomConnectionFactoryProperties connectionFactoryProperties = new CustomConnectionFactoryProperties();
        connectionFactoryProperties.setAddresses("localhost");
        connectionFactoryProperties.setUsername("guest");
        connectionFactoryProperties.setPassword("guest");
        connectionFactoryProperties.setVirtualHost("/IT");
        connectionFactoryProperties.setBeanName("customConnectionFactory");
        connectionFactoryProperties.setApiPort(17562);
        return connectionFactoryProperties;
    }

    default BeanReferenceConnectionFactoryProperties getCompleteBeanReferenceConnectionFactoryProperties()
    {
        BeanReferenceConnectionFactoryProperties connectionFactoryProperties = new BeanReferenceConnectionFactoryProperties();
        connectionFactoryProperties.setBeanName("customConnectionFactoryBeanName");
        return connectionFactoryProperties;
    }

    default ExchangeProperties getCompleteExchangeProperties()
    {
        return getCompleteExchangeProperties("SpringExchange");
    }

    default ExchangeProperties getCompleteExchangeProperties(String exchangeName)
    {
        ExchangeProperties exchangeProperties = new ExchangeProperties();
        exchangeProperties.setName(exchangeName);
        exchangeProperties.setType(ExchangeType.TOPIC);
        return exchangeProperties;
    }

    default MessageConverterProperties getCompleteMessageConverterProperties()
    {
        return getCompleteMessageConverterProperties("xmlMarshallerBeanName");
    }

    default MessageConverterProperties getCompleteMessageConverterProperties(String beanName)
    {
        MessageConverterProperties messageConverterProperties = new MessageConverterProperties();
        messageConverterProperties.setBeanName(beanName);
        return messageConverterProperties;
    }

    default RabbitProperties getCompleteRabbitProperties()
    {
        RabbitProperties rabbitProperties = new RabbitProperties();
        rabbitProperties.setAddresses("localhost");
        rabbitProperties.setUsername("guest");
        rabbitProperties.setPassword("guest");
        rabbitProperties.setVirtualHost("/IT");
        return rabbitProperties;
    }

    default Message getDummyMessage()
    {
        return new Message("{test:\"test\"}".getBytes(), new MessageProperties());
    }

    default Message getDummyMessage(Consumer<Message> consumer)
    {
        Message message = getDummyMessage();
        consumer.accept(message);
        return message;
    }
}
