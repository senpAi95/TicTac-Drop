package com.tictac.droptoken.inject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tictac.droptoken.GridOperations;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class DropTokenConfiguration extends Configuration implements DependencyInjectionConfiguration{
    @Override
    public List<Class<?>> getSingletons() {
        final List<Class<?>> result = new ArrayList();
        // result.add(GridOperations.class);
        return result;
    }

    @Override
    public List<NamedProperty<? extends Object>> getNamedProperties() {
        final List<NamedProperty<? extends Object>> result = new ArrayList<>();
        return result;
    }

}
