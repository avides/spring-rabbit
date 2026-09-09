package com.avides.spring.rabbit.test.support;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A message-type that is itself generic, i.e. one a listener can only read correctly if its type-argument survives the type-resolution.
 *
 * @param <E> type of the content
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TestBox<E>
{
    private String label;

    private E content;
}
