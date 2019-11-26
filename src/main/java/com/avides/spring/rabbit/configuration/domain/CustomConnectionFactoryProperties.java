package com.avides.spring.rabbit.configuration.domain;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Validated
@Getter
@Setter
@ToString
public class CustomConnectionFactoryProperties
{
    @NotBlank
    private String addresses;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String virtualHost = "/";

    @NotBlank
    private String beanName;

    @Range(min = 1, max = 65535)
    private int apiPort = 15672;
}
