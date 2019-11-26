package com.avides.spring.rabbit.configuration.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Validated
@Getter
@Setter
@ToString
public class RabbitAdminProperties
{
    @NotBlank
    private String beanName = "rabbitAdmin";

    @Valid
    private BeanReferenceConnectionFactoryProperties connectionFactory;
}
