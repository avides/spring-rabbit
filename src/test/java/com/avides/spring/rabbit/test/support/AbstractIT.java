package com.avides.spring.rabbit.test.support;

import java.util.Collections;

import javax.ws.rs.core.Application;

import org.junit.runner.RunWith;
import org.springframework.amqp.support.converter.MarshallingMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import com.avides.spring.rabbit.converter.SpringRabbitJsonMessageConverter;
import com.itelg.spring.xom.unmarshaller.XomUnmarshaller;
import com.itelg.spring.xom.unmarshaller.configuration.EnableXomUnmarshaller;
import com.itelg.spring.xom.unmarshaller.parser.Parser;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import nu.xom.Element;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbstractIT.TestConfiguration.class, DummyListenerZero.class, DummyListenerOne.class, TestClassListener.class, OtherTestClassListener.class, TestClassContextAwareListener.class })
public abstract class AbstractIT
{
    @Autowired
    protected GenericApplicationContext applicationContext;

    @Value("${embedded.container.rabbitmq.host}")
    protected String host;

    @EnableAutoConfiguration
    @Configuration
    protected static class TestConfiguration
    {
        // CountingRabbitListener uses the meterRegistry; if none exists everything works fine but causes error logs
        @Bean
        public MeterRegistry simpleMeterRegistry()
        {
            return new SimpleMeterRegistry();
        }

        @Bean
        public Marshaller jaxbMarshaller()
        {
            Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
            marshaller.setPackagesToScan(Application.class.getPackage().getName());
            marshaller.setMarshallerProperties(Collections.singletonMap(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE));
            return marshaller;
        }

        @ConditionalOnBean(Parser.class)
        @EnableXomUnmarshaller
        public class XomMarshallerConfiguration
        {
            @Bean
            public MessageConverter messageConverter(XomUnmarshaller xomUnmarshaller)
            {
                MarshallingMessageConverter converter = new MarshallingMessageConverter();
                converter.setUnmarshaller(xomUnmarshaller);
                converter.setMarshaller(jaxbMarshaller());
                return converter;
            }

            @Bean
            public MessageConverter otherMessageConverter(XomUnmarshaller xomUnmarshaller)
            {
                MarshallingMessageConverter converter = new MarshallingMessageConverter();
                converter.setUnmarshaller(xomUnmarshaller);
                converter.setMarshaller(jaxbMarshaller());
                return converter;
            }

            @Bean
            public MessageConverter springRabbitJsonMessageConverter()
            {
                return new SpringRabbitJsonMessageConverter();
            }
        }

        @Component
        public class DummyParser implements Parser<String>
        {
            @Override
            public String parse(Element arg0)
            {
                return arg0.getValue();
            }
        }
    }
}
