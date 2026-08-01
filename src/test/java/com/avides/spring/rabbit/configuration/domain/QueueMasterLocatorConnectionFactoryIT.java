package com.avides.spring.rabbit.configuration.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.test.context.ActiveProfiles;

import com.avides.spring.rabbit.test.support.AbstractIT;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

/**
 * Covers the management-API branch of {@link QueueMasterLocatorConnectionFactory}, which needs a broker that answers on its management port.
 */
@ActiveProfiles("it")
class QueueMasterLocatorConnectionFactoryIT extends AbstractIT
{
    private static final String QUEUE_KEY = "[com.avides.spring.rabbit.queue.zero]";

    @Value("${embedded.container.rabbitmq.port}")
    private int amqpPort;

    @Value("${embedded.container.rabbitmq.management-port}")
    private int managementPort;

    private CachingConnectionFactory defaultConnectionFactory;

    private ListAppender<ILoggingEvent> logAppender;

    private Logger logger;

    @BeforeEach
    void setUp()
    {
        defaultConnectionFactory = new CachingConnectionFactory(host, amqpPort);
        defaultConnectionFactory.setUsername("guest");
        defaultConnectionFactory.setVirtualHost("/");

        logAppender = new ListAppender<>();
        logAppender.start();
        logger = (Logger) LoggerFactory.getLogger(QueueMasterLocatorConnectionFactory.class);
        logger.addAppender(logAppender);
    }

    @AfterEach
    void tearDown()
    {
        logger.detachAppender(logAppender);
        defaultConnectionFactory.destroy();
    }

    @Test
    void testGetTargetConnectionFactoryWithoutMonitoringPermission()
    {
        var rabbitProperties = new RabbitProperties();
        rabbitProperties.setPassword("wrongPassword");
        var connectionFactory = new QueueMasterLocatorConnectionFactory(defaultConnectionFactory, rabbitProperties, managementPort);

        var result = connectionFactory.getTargetConnectionFactory(QUEUE_KEY);

        // the master node cannot be determined, so the default connection factory is used
        assertThat(result).isSameAs(defaultConnectionFactory);
        assertThat(logAppender.list).anyMatch(event -> event.getFormattedMessage().contains("needs 'monitoring'-role"));
    }

    @Test
    void testGetTargetConnectionFactoryWithUnreachableManagementApi()
    {
        var rabbitProperties = new RabbitProperties();
        rabbitProperties.setPassword("guest");
        var connectionFactory = new QueueMasterLocatorConnectionFactory(defaultConnectionFactory, rabbitProperties, amqpPort);

        var result = connectionFactory.getTargetConnectionFactory(QUEUE_KEY);

        // a management API that does not answer must not be reported as a permission problem
        assertThat(result).isSameAs(defaultConnectionFactory);
        assertThat(logAppender.list).noneMatch(event -> event.getFormattedMessage().contains("needs 'monitoring'-role"));
        assertThat(logAppender.list).anyMatch(event -> event.getFormattedMessage().contains("Failed to fetch queue-master"));
    }
}
