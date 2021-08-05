package com.tictac.drop;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.dropwizard.Application;
import io.dropwizard.jersey.errors.EarlyEofExceptionMapper;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ru.vyarus.dropwizard.guice.GuiceBundle;


import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 *
 */
public class TicTacDropApplication extends Application<TicTacDropConfiguration> {

    private static final String DATABASE = "drop";
    private static final String DB_CONNECTION_STRING = "mongodb://localhost:27017";

    public static void main(String[] args) throws Exception {
        new TicTacDropApplication().run(args);
    }

    @Override
    public String getName() {
        return "Drop Token";
    }

    @Override
    public void initialize(Bootstrap<TicTacDropConfiguration> bootstrap) {
        TicTacDropModule ticTacDropModule = new TicTacDropModule();

        bootstrap.addBundle(GuiceBundle.builder()
                .enableAutoConfig(getClass().getPackage().getName())
                .modules(ticTacDropModule)
                .build());
    }

    @Override
    public void run(TicTacDropConfiguration configuration,
                    Environment environment) {
        environment.getObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new Jdk8Module());
        environment.getObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        environment.jersey().register(new TicTacDropExceptionMapper());
        environment.jersey().register(new WebApplicationExceptionMapper());
        environment.jersey().register(new JerseyViolationExceptionMapper());
        environment.jersey().register(new JsonProcessingExceptionMapper());
        environment.jersey().register(new EarlyEofExceptionMapper());

    }

}
