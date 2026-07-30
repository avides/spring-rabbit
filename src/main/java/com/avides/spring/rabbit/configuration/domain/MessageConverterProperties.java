package com.avides.spring.rabbit.configuration.domain;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Validated
@Setter
@Getter
@ToString
public class MessageConverterProperties
{
    @NotBlank
    private String beanName;
}
