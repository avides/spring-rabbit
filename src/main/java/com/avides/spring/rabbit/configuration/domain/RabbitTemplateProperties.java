package com.avides.spring.rabbit.configuration.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Validated
@Setter
@Getter
@ToString
public class RabbitTemplateProperties
{
    @Valid
    private ExchangeProperties exchange;

    @NotBlank
    private String routingkey;

    @NotBlank
    private String beanName;

    @Valid
    private MessageConverterProperties messageConverter;

    @Valid
    private BeanReferenceConnectionFactoryProperties connectionFactory;
}
