package com.avides.spring.rabbit.configuration.domain;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.connection.RoutingConnectionFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.QueueInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * A {@link RoutingConnectionFactory} that determines the node on which a queue is located and returns a factory that connects directly to that node. The
 * RabbitMQ management plugin is called over REST to determine the node and the corresponding address for that node is injected into the connection factory. A
 * single instance of each connection factory is retained in a cache. If the location cannot be determined, the default connection factory is returned.
 */
@Slf4j
public class QueueMasterLocatorConnectionFactory implements ConnectionFactory, RoutingConnectionFactory, DisposableBean
{
    private ConnectionFactory defaultConnectionFactory;

    private RabbitProperties rabbitProperties;

    private Map<String, ConnectionFactory> nodeConnectionFactories = new HashMap<>();

    private int apiPort;

    /**
     * Initializes an instance of the {@link QueueMasterLocatorConnectionFactory}
     *
     * @param defaultConnectionFactory {@link ConnectionFactory} which is used if no master node could be determined
     * @param rabbitProperties The {@link RabbitProperties} to resolve environment properties
     * @param apiPort The port of the REST-API
     */
    public QueueMasterLocatorConnectionFactory(ConnectionFactory defaultConnectionFactory, RabbitProperties rabbitProperties, int apiPort)
    {
        this.defaultConnectionFactory = defaultConnectionFactory;
        this.rabbitProperties = rabbitProperties;
        this.apiPort = apiPort;
    }

    @Override
    public ConnectionFactory getTargetConnectionFactory(Object key)
    {
        String queueName = ((String) key);
        queueName = queueName.substring(1, queueName.length() - 1);
        Assert.isTrue(!queueName.contains(","), "Cannot use QueueMasterLocatorConnectionFactory with more than one queue: " + key);

        ConnectionFactory queueMasterConnectionFactory = getConnectionFactoryForQueue(queueName);
        return queueMasterConnectionFactory != null ? queueMasterConnectionFactory : defaultConnectionFactory;
    }

    private ConnectionFactory getConnectionFactoryForQueue(String queueName)
    {
        String queueMasterNode = resolveMasterNodeForQueue(getQueueInfo(queueName));

        if (queueMasterNode != null)
        {
            if (nodeConnectionFactories.get(queueMasterNode) == null)
            {
                CachingConnectionFactory nodeConnectionFactory = new CachingConnectionFactory();
                nodeConnectionFactory.setBeanName("rabbitNodeConnectionFactory:" + queueMasterNode);
                nodeConnectionFactory.setHost(queueMasterNode);
                nodeConnectionFactory.setPort(rabbitProperties.determinePort());
                nodeConnectionFactory
                        .setVirtualHost(StringUtils.hasText(rabbitProperties.determineVirtualHost()) ? rabbitProperties.determineVirtualHost() : "/");
                nodeConnectionFactory.setUsername(rabbitProperties.determineUsername());
                nodeConnectionFactory.setPassword(rabbitProperties.determinePassword());
                nodeConnectionFactories.put(queueMasterNode, nodeConnectionFactory);
                log.debug("Created connectionFactory for node " + queueMasterNode);
            }

            return nodeConnectionFactories.get(queueMasterNode);
        }

        return null;
    }

    private QueueInfo getQueueInfo(String queueName)
    {
        try
        {
            Client client = new Client("http://" + getHost() + ":" + apiPort + "/api/", getUsername(), rabbitProperties.getPassword());
            return client.getQueue(getVirtualHost(), queueName);

        }
        catch (HttpClientErrorException e)
        {
            if (e.getRawStatusCode() == 401)
            {
                log.warn("Rabbit-user needs 'monitoring'-role to fetch information for " + queueName, e);
            }
            else
            {
                log.warn("Failed to fetch queue-master for " + queueName, e);
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to fetch queue-master for " + queueName, e);
        }

        return null;
    }

    // @VisibleForTesting
    String resolveMasterNodeForQueue(QueueInfo queueInfo)
    {
        if (queueInfo == null)
        {
            return null;
        }

        if (queueInfo.isExclusive())
        {
            // LIBS-530: this::createConnection is called before this::getConnectionFactoryForQueue -> Connection already exists for the exclusive queue
            // -> Queue is locked
            return null;
        }
        return queueInfo.getNode().replaceAll("rabbit@", "");
    }

    @Override
    public Connection createConnection()
    {
        return defaultConnectionFactory.createConnection();
    }

    @Override
    public String getHost()
    {
        return defaultConnectionFactory.getHost();
    }

    @Override
    public int getPort()
    {
        return defaultConnectionFactory.getPort();
    }

    @Override
    public String getVirtualHost()
    {
        return defaultConnectionFactory.getVirtualHost();
    }

    @Override
    public String getUsername()
    {
        return defaultConnectionFactory.getUsername();
    }

    @Override
    public void addConnectionListener(ConnectionListener listener)
    {
        defaultConnectionFactory.addConnectionListener(listener);
    }

    @Override
    public boolean removeConnectionListener(ConnectionListener listener)
    {
        return defaultConnectionFactory.removeConnectionListener(listener);
    }

    @Override
    public void clearConnectionListeners()
    {
        defaultConnectionFactory.clearConnectionListeners();
    }

    @Override
    public void destroy() throws Exception
    {
        for (ConnectionFactory connectionFactory : nodeConnectionFactories.values())
        {
            ((DisposableBean) connectionFactory).destroy();
        }
    }
}
