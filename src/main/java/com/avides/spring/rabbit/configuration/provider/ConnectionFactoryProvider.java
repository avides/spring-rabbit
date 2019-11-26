package com.avides.spring.rabbit.configuration.provider;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import com.avides.spring.rabbit.configuration.domain.QueueMasterLocatorConnectionFactory;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Provider of {@link ConnectionFactory}.
 */
@UtilityClass
@Slf4j
public class ConnectionFactoryProvider
{
    /**
     * Creates a new {@link QueueMasterLocatorConnectionFactory} or just returns the given `defaultConnectionFactory` depending on the configured host. If it is
     * likely that the host belongs to a rabbit cluster, a {@link QueueMasterLocatorConnectionFactory} shall be used.
     * <p>
     * To keep it simple this method always returns `defaultConnectionFactory` if multiple hosts are configured.
     *
     * @param defaultConnectionFactory the default {@link ConnectionFactory} which is used if the queue master could not be resolved
     * @param rabbitProperties the wrapper object to hold some configurations(e.g. user, password)
     * @param apiPort the port of the API
     * @return the created {@link ConnectionFactory}
     */
    public ConnectionFactory createConnectionFactory(ConnectionFactory defaultConnectionFactory, RabbitProperties rabbitProperties, int apiPort)
    {
        String host = rabbitProperties.determineHost();

        // avoid multiple IP's for IPv6 enabled OS
        if (!"localhost".equals(host) && !rabbitProperties.determineAddresses().contains(","))
        {
            try
            {
                InetAddress[] inetAddresses = InetAddress.getAllByName(host);
                if (inetAddresses.length > 1)
                {
                    log.debug("Create new QueueMasterLocatorConnectionFactory for nodes: {}", (Object) inetAddresses);
                    return new QueueMasterLocatorConnectionFactory(defaultConnectionFactory, rabbitProperties, apiPort);
                }
            }
            catch (UnknownHostException e)
            {
                log.warn("Cannot rersolve domain: {}", host, e);
            }
        }

        log.debug("Use defaultConnectionFactory: {}", defaultConnectionFactory);
        return defaultConnectionFactory;
    }
}
