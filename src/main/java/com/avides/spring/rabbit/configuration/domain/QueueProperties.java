package com.avides.spring.rabbit.configuration.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Validated
@Setter
@Getter
@ToString
public class QueueProperties
{
    /**
     * Configures if the queue and its listener should be created. The possible existing queue will not be deleted!
     */
    private boolean creationEnabled = true;

    private String routingkey;

    private List<String> routingkeys;

    @NotBlank
    private String name;

    @Min(1)
    private int limit;

    private boolean durable = true;

    private boolean exclusive;

    private Map<String, Object> arguments = new HashMap<>();

    @Valid
    private ExchangeProperties exchange;

    @NotNull
    private RabbitAdminProperties rabbitAdmin = new RabbitAdminProperties();

    @Valid
    private ListenerProperties listener;
}
