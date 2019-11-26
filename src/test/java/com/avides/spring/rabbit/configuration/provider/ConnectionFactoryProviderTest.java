package com.avides.spring.rabbit.configuration.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.assertj.core.api.ObjectAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import com.avides.spring.rabbit.configuration.domain.QueueMasterLocatorConnectionFactory;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ ConnectionFactoryProvider.class, InetAddress.class })
public class ConnectionFactoryProviderTest
{
    @MockStrict
    private ConnectionFactory defaultConnectionFactory;

    private final RabbitProperties rabbitProperties = new RabbitProperties();

    private final int apiPort = 15762;

    @Test
    public void testCreateConnectionFactoryWithDomainWithMultipleIps() throws Exception
    {
        assertCreatedConnectionFactoryFor("cluster.example.com", withMultipleIpsFor("cluster.example.com"))
                .isInstanceOf(QueueMasterLocatorConnectionFactory.class);
    }

    @Test
    public void testCreateConnectionFactoryWithDomainAndPortWithMultipleIps() throws Exception
    {
        assertCreatedConnectionFactoryFor("cluster.example.com:5672", withMultipleIpsFor("cluster.example.com"))
                .isInstanceOf(QueueMasterLocatorConnectionFactory.class);
    }

    @Test
    public void testCreateConnectionFactoryWithDomainWithSingleIp() throws Exception
    {
        assertCreatedConnectionFactoryFor("node.example.com", withSingleIpFor("node.example.com"))
                .isSameAs(defaultConnectionFactory);
    }

    @Test
    public void testCreateConnectionFactoryWithDomainAndPortWithSingleIp() throws Exception
    {
        assertCreatedConnectionFactoryFor("node.example.com:5672", withSingleIpFor("node.example.com"))
                .isSameAs(defaultConnectionFactory);
    }

    @Test
    public void testCreateConnectionFactoryWithLocalhost() throws Exception
    {
        assertCreatedConnectionFactoryFor("localhost", withoutHostnameResolution())
                .isSameAs(defaultConnectionFactory);
    }

    @Test
    public void testCreateConnectionFactoryWithDomainWithIp() throws Exception
    {
        assertCreatedConnectionFactoryFor("127.0.0.1", withSingleIpFor("127.0.0.1"))
                .isSameAs(defaultConnectionFactory);
    }

    @Test
    public void testCreateConnectionFactoryWithMultipleAddresses() throws Exception
    {
        assertCreatedConnectionFactoryFor("cluster.example.com,node.example.com:5672", withoutHostnameResolution())
                .isSameAs(defaultConnectionFactory);
    }

    @Test
    public void testCreateConnectionFactoryWithDomainWithoutAnyHost() throws Exception
    {
        assertCreatedConnectionFactoryFor(null, withoutHostnameResolution())
                .isSameAs(defaultConnectionFactory);
    }

    @Test
    public void testCreateConnectionFactoryWithUnknownHost() throws Exception
    {
        assertCreatedConnectionFactoryFor("unknown.example.com", withUnknownHostFor("unknown.example.com"))
                .isSameAs(defaultConnectionFactory);
    }

    private ObjectAssert<ConnectionFactory> assertCreatedConnectionFactoryFor(String addresses, INetAddressGetAllByNameExpectation expectation)
            throws UnknownHostException
    {
        mockStatic(InetAddress.class);

        expectation.expect();

        replayAll();

        if (addresses != null)
        {
            rabbitProperties.setAddresses(addresses);
        }
        ConnectionFactory connectionFactory = ConnectionFactoryProvider.createConnectionFactory(defaultConnectionFactory, rabbitProperties, apiPort);

        verifyAll();

        return assertThat(connectionFactory);
    }

    private static INetAddressGetAllByNameExpectation withSingleIpFor(String hostname)
    {
        return () -> expect(InetAddress.getAllByName(hostname)).andReturn(new InetAddress[] { createStrictMock(InetAddress.class) });
    }

    private static INetAddressGetAllByNameExpectation withMultipleIpsFor(String hostname)
    {
        return () -> expect(InetAddress.getAllByName(hostname))
                .andReturn(new InetAddress[] { createStrictMock(InetAddress.class), createStrictMock(InetAddress.class) });
    }

    private static INetAddressGetAllByNameExpectation withUnknownHostFor(String hostname)
    {
        return () -> expect(InetAddress.getAllByName(hostname)).andThrow(new UnknownHostException());
    }

    private static INetAddressGetAllByNameExpectation withoutHostnameResolution()
    {
        return () ->
        {
            // expect nothing
        };
    }

    @FunctionalInterface
    private interface INetAddressGetAllByNameExpectation
    {
        void expect() throws UnknownHostException;
    }
}
