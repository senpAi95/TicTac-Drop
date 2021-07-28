package com.tictac.droptoken.inject;

import com.tictac.droptoken.GridOperations;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class DependencyInjectionBundle implements ConfiguredBundle<DependencyInjectionConfiguration> {

    @Override
    public void run(DependencyInjectionConfiguration configuration, Environment environment) {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                 bind(new GridOperations()).to(GridOperations.class);
                for(Class<?> singletonClass : configuration.getSingletons()) {
                    bindAsContract(singletonClass).in(Singleton.class);
                }
                for (NamedProperty<? extends Object> namedProperty : configuration.getNamedProperties()) {
                    bind((Object) namedProperty.getValue()).to((Class<Object>) namedProperty.getClazz()).named(namedProperty.getId());
                }
            }
        });
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }
}
