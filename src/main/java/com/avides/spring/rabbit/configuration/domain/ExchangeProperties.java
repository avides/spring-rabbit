package com.avides.spring.rabbit.configuration.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Validated
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ExchangeProperties
{
    @NotBlank
    private String name;

    @NotNull
    private ExchangeType type = ExchangeType.TOPIC;

    public enum ExchangeType
    {
        TOPIC,
        DIRECT;
    }
}
