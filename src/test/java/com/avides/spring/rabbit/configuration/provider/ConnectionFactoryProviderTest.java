package com.avides.spring.rabbit.configuration.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.util.StringUtils;

import com.avides.spring.rabbit.configuration.domain.QueueMasterLocatorConnectionFactory;

public class ConnectionFactoryProviderTest
{
    private final ConnectionFactory defaultConnectionFactory = mock(ConnectionFactory.class);

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

    private ObjectAssert<ConnectionFactory> assertCreatedConnectionFactoryFor(String addresses, INetAddressGetAllByNameExpectation expectation) throws Exception
    {
        try (MockedStatic<InetAddress> inetAddress = mockStatic(InetAddress.class))
        {
            expectation.expect(inetAddress);

            if (addresses != null)
            {
                rabbitProperties.setAddresses(splitAddresses(addresses));
            }
            ConnectionFactory connectionFactory = ConnectionFactoryProvider.createConnectionFactory(defaultConnectionFactory, rabbitProperties, apiPort);

            return assertThat(connectionFactory);
        }
    }

    private static List<String> splitAddresses(String addresses)
    {
        return Arrays.stream(StringUtils.commaDelimitedListToStringArray(addresses))
                .map(String::strip)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    private static INetAddressGetAllByNameExpectation withSingleIpFor(String hostname)
    {
        return inetAddress -> inetAddress.when(() -> InetAddress.getAllByName(hostname)).thenReturn(new InetAddress[] { mock(InetAddress.class) });
    }

    private static INetAddressGetAllByNameExpectation withMultipleIpsFor(String hostname)
    {
        return inetAddress -> inetAddress.when(() -> InetAddress.getAllByName(hostname))
                .thenReturn(new InetAddress[] { mock(InetAddress.class), mock(InetAddress.class) });
    }

    private static INetAddressGetAllByNameExpectation withUnknownHostFor(String hostname)
    {
        return inetAddress -> inetAddress.when(() -> InetAddress.getAllByName(hostname)).thenThrow(new UnknownHostException());
    }

    private static INetAddressGetAllByNameExpectation withoutHostnameResolution()
    {
        return inetAddress ->
        {
            // expect nothing
        };
    }

    @FunctionalInterface
    private interface INetAddressGetAllByNameExpectation
    {
        void expect(MockedStatic<InetAddress> inetAddress) throws UnknownHostException;
    }
}
