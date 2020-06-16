# Spring-Rabbit

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.avides.spring/spring-rabbit/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.avides.spring/spring-rabbit)
[![Build](https://github.com/avides/spring-rabbit/workflows/release/badge.svg)](https://github.com/avides/spring-rabbit/actions)
[![Nightly build](https://github.com/avides/spring-rabbit/workflows/nightly/badge.svg)](https://github.com/avides/spring-rabbit/actions)
[![Coverage report](https://sonarcloud.io/api/project_badges/measure?project=avides_spring-rabbit&metric=coverage)](https://sonarcloud.io/dashboard?id=avides_spring-rabbit)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=avides_spring-rabbit&metric=alert_status)](https://sonarcloud.io/dashboard?id=avides_spring-rabbit)
[![Technical dept](https://sonarcloud.io/api/project_badges/measure?project=avides_spring-rabbit&metric=sqale_index)](https://sonarcloud.io/dashboard?id=avides_spring-rabbit)

### Dependency
``` xml
<dependency>
	<groupId>com.avides.spring</groupId>
	<artifactId>spring-rabbit</artifactId>
	<version>2.2.0</version>
</dependency>
```

## Table of Contents
*  [Rabbit-Configuration](#rabbit-configuration)
*  [Known issues](#known-issues)
*  [Dependencies](#dependencies)
*  [Metrics](#metrics)
*  [MessagePostProcessors](#messagepostprocessors)
*  [Example](#example)
*  [DefaultProperties](#defaultproperties)
*  [QueueProperties](#queueproperties)
*  [RabbitTemplateProperties](#rabbittemplateproperties)
*  [RabbitAdminProperties](#rabbitadminproperties)
*  [MessageConverterProperties](#messageconverterproperties)
*  [ExchangeProperties](#exchangeproperties)
*  [ListenerProperties](#listenerproperties)
*  [CustomConnectionFactoryProperties](#customconnectionfactoryproperties)
*  [BeanReferenceConnectionFactoryProperties](#beanreferenceconnectionfactoryproperties)

## Rabbit-Configuration

``` ini
docker run \
-d \
--name rabbitmq \
--hostname rabbitmq \
--restart=always \
--memory=500MB \
-v PATH:/var/lib/rabbitmq \
-e RABBITMQ_NODENAME=rabbit@localhost \
-p 5672:5672 \
-p 15672:15672 \
rabbitmq:VERSION
```

The ´RABBITMQ_NODENAME´ environment variable is necessary!

The rabbit user needs at least ´MONITORING´ as tag!

## Known issues

The rabbit template needs to be autowired with ´@Lazy´!

If one property needs to be overridden in other profiles the complete prefix/collection needs to be copied!

## Dependencies

This project needs `micrometer-core` as dependency with a bean of `meterRegistry`.

## Metrics

`counter_rabbit_listener_event_count`

`counter_rabbit_listener_event_total_duration_milliseconds_count`

`rabbit_outbound_message_total` - Counter for each outbound grouped by the bean name of the rabbit template

## MessagePostProcessors
### appIdEnricherMessagePostProcessor
MessagePostProcessor that adds the configured app-id as the appId-Header.
Prefers the value of `spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.app-id` as app-id. If no property value exists, `info.artifactId` or else `UNKNOWN` is used.
Could be disabled by setting `spring.rabbitmq.outbound.global.before-publish-post-processor.appid-enricher.enabled` to false.

### counting-outbound
MessagePostProcessor that adds metrics for each outbound message.
Enabled if `spring.rabbitmq.outbound.global.before-publish-post-processor.counting-outbound.enabled` is true or not specified.
Using MeterRegistry will increment a counter with the bean name of the template as a tag for each message.

## Configuration

## Example

Example for one connection factory

``` ini
spring.rabbitmq.addresses=localhost
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

spring.rabbitmq.exchange.name=com.example.exchange

spring.rabbitmq.queues[0].name=com.example.queue.zero
spring.rabbitmq.queues[0].routing-keys[0]=routingkey.zero.zero
spring.rabbitmq.queues[0].routing-keys[1]=routingkey.zero.one
spring.rabbitmq.queues[0].routing-keys[2]=routingkey.zero.two.*
spring.rabbitmq.queues[0].limit=500000
spring.rabbitmq.queues[0].listener.bean-name=myListenerZero

spring.rabbitmq.queues[1].name=com.example.queue.one
spring.rabbitmq.queues[1].routing-key=routingkey.one
spring.rabbitmq.queues[1].limit=100000
spring.rabbitmq.queues[1].listener.bean-name=myListenerOne

spring.rabbitmq.outbounds[0].bean-name=myRabbitTemplateZero
spring.rabbitmq.outbounds[0].routing-key=another.routingkey.zero

spring.rabbitmq.outbounds[1].bean-name=myRabbitTemplateOne
spring.rabbitmq.outbounds[1].routing-key=another.routingkey.one.{placeholder}

spring.rabbitmq.outbounds[2].bean-name=myRabbitTemplateTwo
spring.rabbitmq.outbounds[2].routing-key=another.routingkey.two
spring.rabbitmq.outbounds[2].exchange.name=com.example.another.exchange
```

Example for multiple connection factories

``` ini
spring.rabbitmq.connections[0].addresses=localhost
spring.rabbitmq.connections[0].username=guest
spring.rabbitmq.connections[0].password=guest
spring.rabbitmq.connections[0].virtual-host=/
spring.rabbitmq.connections[0].bean-name=myConnectionFactoryZero

spring.rabbitmq.connections[1].addresses=localhost
spring.rabbitmq.connections[1].username=guest
spring.rabbitmq.connections[1].password=guest
spring.rabbitmq.connections[1].virtual-host=/
spring.rabbitmq.connections[1].bean-name=myConnectionFactoryOne

spring.rabbitmq.message-converter.bean-name=myMessageConverter
spring.rabbitmq.exchange.name=com.example.exchange

#myConnectionFactoryZero
spring.rabbitmq.queues[0].name=com.example.queue.zero
spring.rabbitmq.queues[0].routing-key=routingkey.zero
spring.rabbitmq.queues[0].exchange.name=com.example.exchange.zero
spring.rabbitmq.queues[0].exchange.type=DIRECT
spring.rabbitmq.queues[0].limit=500000
spring.rabbitmq.queues[0].listener.bean-name=myListenerZero
spring.rabbitmq.queues[0].listener.max-concurrent-consumers=2
spring.rabbitmq.queues[0].rabbit-admin.bean-name=myRabbitAdminZero
spring.rabbitmq.queues[0].rabbit-admin.connection-factory.bean-name=myConnectionFactoryZero

spring.rabbitmq.outbounds[0].bean-name=myRabbitTemplateZero
spring.rabbitmq.outbounds[0].routing-key=another.routingkey.zero
spring.rabbitmq.outbounds[0].connection-factory.bean-name=myConnectionFactoryZero
spring.rabbitmq.outbounds[0].exchange.name=com.example.exchange.zero
spring.rabbitmq.outbounds[0].exchange.type=DIRECT

#myConnectionFactoryOne
spring.rabbitmq.queues[1].name=com.example.queue.one
spring.rabbitmq.queues[1].routing-key=routingkey.one
spring.rabbitmq.queues[1].limit=10000
spring.rabbitmq.queues[1].listener.bean-name=myListenerOne
spring.rabbitmq.queues[1].listener.message-converter.bean-name=myConverterOne
spring.rabbitmq.queues[1].rabbit-admin.bean-name=myRabbitAdminOne
spring.rabbitmq.queues[1].rabbit-admin.connection-factory.bean-name=myConnectionFactoryOne

spring.rabbitmq.queues[2].name=com.example.queue.two
spring.rabbitmq.queues[2].routing-keys[0]=routingkey.two.zero
spring.rabbitmq.queues[2].routing-keys[1]=routingkey.two.one
spring.rabbitmq.queues[2].limit=500000
spring.rabbitmq.queues[2].listener.bean-name=myListenerTwo
spring.rabbitmq.queues[2].rabbit-admin.bean-name=myRabbitAdminOne
spring.rabbitmq.queues[2].rabbit-admin.connection-factory.bean-name=myConnectionFactoryOne

spring.rabbitmq.outbounds[1].bean-name=myRabbitTemplateOne
spring.rabbitmq.outbounds[1].routing-key=another.routingkey.one
spring.rabbitmq.outbounds[1].connection-factory.bean-name=myConnectionFactoryOne
```

Example for an implementation of a SpringRabbitListener

```java
@Component
public class MyListener extends AbstractSpringRabbitListener<CoreData>
{
    @Autowired
    private MyService myService;

    @Override
    protected void handleEvent(MyData myData)
    {
        myDataService.doSomething(myData);
    }
}
```
## DefaultProperties

This properties are used to set default values. If there are specific properties (e.g `spring.rabbitmq.queues[0].exchange.(ExchangeProperties)`) the specefic properties are used.

### spring.rabbitmq.exchange.(ExchangeProperties)

See [ExchangeProperties](#exchangeproperties)

### spring.rabbitmq.message-converter.(MessageConverterProperties)

See [MessageConverterProperties](#messageconverterproperties)

### spring.rabbitmq.max-concurrent-consumers

@Min(1)

Default 1

``` ini
spring.rabbitmq.max-concurrent-consumers=2
```

### spring.rabbitmq.api-port

@Range(min = 1, max = 65535)

Default 15672

``` ini
spring.rabbitmq.api-port=65535
```

## QueueProperties

#### spring.rabbitmq.queues[0].creation-enabled

Configures if the queue and its listener should be created. The possible existing queue will not be deleted!

Default: true

``` ini
spring.rabbitmq.queues[0].creation-enabled=false
```

#### spring.rabbitmq.queues[0].name

@NotBlank

Configures the name of the queue. The name is also used to create the DLX (name + ".dlx").

``` ini
spring.rabbitmq.queues[0].name=com.example.queue
```

#### spring.rabbitmq.queues[0].routing-key

Configures the binding of the queue to the exchange.
Either `routing-key` or `routing-keys[*]` is necessary.

``` ini
spring.rabbitmq.queues[0].routing-key=example.routingkey
```

#### spring.rabbitmq.queues[0].routing-keys[0]

Configures the bindings of the queue to the exchange.
Either `routing-key` or `routing-keys[*]` is necessary.

``` ini
spring.rabbitmq.queues[0].routing-keys[0]=example.routingkey.one
spring.rabbitmq.queues[0].routing-keys[1]=example.routingkey.two
spring.rabbitmq.queues[0].routing-keys[2]=example.routingkey.three.*
```

#### spring.rabbitmq.queues[0].limit

@Min(1)

Configures the limit of the queue and his DLX and is necessary (min value of 1).


``` ini
spring.rabbitmq.queues[0].limit=10000
```

#### spring.rabbitmq.queues[0].durable

Configures the Queue.durable property.
Default is true.
Is used for the queue AND his DLX.


``` ini
spring.rabbitmq.queues[0].durable=false
```

#### spring.rabbitmq.queues[0].exclusive

Configures the Queue.exclusive.
Default is false.
Is used for the queue AND his DLX.


``` ini
spring.rabbitmq.queues[0].exclusive=false
```

#### spring.rabbitmq.queues[0].arguments[someAdditionalQueueArgumentsKey]

Adds additional arguments for the queue.
`x-dead-letter-exchange`, `x-dead-letter-routing-key` and `x-max-length` are added and would override existing entries with the same key.
Is ONLY used for the queue NOT his DLX!


``` ini
spring.rabbitmq.queues[0].arguments[someAdditionalQueueArgumentsKey]=Value
```

#### spring.rabbitmq.queues[0].exchange.(ExchangeProperties)

@Valid

See [ExchangeProperties](#exchangeproperties)

#### spring.rabbitmq.queues[0].rabbit-admin.(RabbitAdminProperties)

@NotNull

See [RabbitAdminProperties](#rabbitadminproperties)

#### spring.rabbitmq.queues[0].listener.(ListenerProperties)

@Valid

See [ListenerProperties](#listenerproperties)

## RabbitTemplateProperties

#### spring.rabbitmq.outbounds[0].bean-name

@NotBlank

Configures the autowired bean name of the rabbit template.
One limit: Needs to be autowired with `@Lazy`!

``` ini
spring.rabbitmq.outbounds[0].bean-name=myRabbitTemplte
```

#### spring.rabbitmq.outbounds[0].routing-key

@NotBlank

Configures the routing key for the rabbit template.

``` ini
spring.rabbitmq.outbounds[0].routing-key=example.routingkey
```

#### spring.rabbitmq.outbounds[0].exchange.(ExchangeProperties)

@Valid

See [ExchangeProperties](#exchangeproperties)

#### spring.rabbitmq.outbounds[0].message-converter.(MessageConverterProperties)

@Valid

See [MessageConverterProperties](#messageconverterproperties)

#### spring.rabbitmq.outbounds[0].connection-factory.(BeanReferenceConnectionFactoryProperties)

@Valid

See [BeanReferenceConnectionFactoryProperties](#beanreferenceconnectionfactoryproperties)

## RabbitAdminProperties

#### .bean-name

@NotBlank

Default: "rabbitAdmin"

``` ini
spring.rabbitmq.queues[0].rabbit-admin.bean-name=myRabbitAdmin
```

#### .connection-factory.(BeanReferenceConnectionFactoryProperties)

See [BeanReferenceConnectionFactoryProperties](#beanreferenceconnectionfactoryproperties)

## MessageConverterProperties

This properties are resolved like this:

- custom properties
- default properties
- check if the context already contains one message converter

#### .bean-name

@NotBlank

The autowired name of the bean.

``` ini
spring.rabbitmq.outbounds[0].message-converter.bean-name=jaxbMessageConverter
```

## ExchangeProperties

#### .name

@NotBlank

The name of the exchange.

``` ini
spring.rabbitmq.exchange.name=com.example
```

#### .type

@NotNull

Default: TOPIC
Values: TOPIC, DIRECT

``` ini
spring.rabbitmq.exchange.type=TOPIC
```

## ListenerProperties

#### .creation-enabled

Configures if the listener should be created.

Default: true

``` ini
spring.rabbitmq.queues[0].listener.creation-enabled=false
```

#### .bean-name

@NotBlank

The name of the SpringRabbitListener.

``` ini
spring.rabbitmq.queues[0].listener.bean-name=myListener
```

#### .message-converter.(MessageConverterProperties)

@Valid

See [MessageConverterProperties](#messageconverterproperties)

#### .max-concurrent-consumers

Could be null or @Min(1)

Default: 1

``` ini
spring.rabbitmq.queues[0].listener.max-concurrent-consumers=2
```

## CustomConnectionFactoryProperties

These properties should be used if more than one connection factory is needed. When used, make sure that the RabbitAutoConfiguration is disabled / excluded.

#### .addresses

@NotBlank

``` ini
spring.rabbitmq.connections[0].addresses=localhost
```

#### .username

@NotBlank

``` ini
spring.rabbitmq.connections[0].username=guest
```

#### .password

@NotBlank

``` ini
spring.rabbitmq.connections[0].password=guest
```

#### .virtual-host

@NotBlank
Default: /

``` ini
spring.rabbitmq.connections[0].virtual-host=integrationtest
```

#### .bean-name

@NotBlank

``` ini
spring.rabbitmq.connections[0].bean-name=myConnectionFactory
```

#### .api-port

@Range(min = 1, max = 65535)
Default: 15672

``` ini
spring.rabbitmq.connections[0].api-port=15673
```

## BeanReferenceConnectionFactoryProperties

#### .bean-name

@NotBlank

Reference name of the specific custom connection factory [CustomConnectionFactoryProperties.bean-name](#customconnectionfactoryproperties.bean-name).

``` ini
spring.rabbitmq.outbounds[1].connection-factory.bean-name=myConnectionFactory
```
