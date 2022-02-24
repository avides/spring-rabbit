package com.avides.spring.rabbit.configuration.domain;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Validated
@Getter
@Setter
@ToString
public class ListenerProperties
{
    /**
     * Configures if the listener should be created
     */
    private boolean creationEnabled = true;

    @NotBlank
    private String beanName;

    @Valid
    private MessageConverterProperties messageConverter;

    @Min(1)
    private Integer prefetchCount;

    @Min(1)
    private Integer maxConcurrentConsumers;
}
