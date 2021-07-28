package com.tictac.droptoken.inject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NamedProperty<T> {
    private final String id;
    private final T value;
    private final Class<T> clazz;

    @JsonCreator
    public NamedProperty(@JsonProperty("id") String id, @JsonProperty("value") T value, @JsonProperty("clazz") Class<T> clazz) {
        this.id = id;
        this.value = value;
        this.clazz = clazz;
    }

    public String getId() {
        return id;
    }

    public T getValue() {
        return value;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
