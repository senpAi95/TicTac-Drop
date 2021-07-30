package com.tictac.droptoken;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tictac.droptoken.codec.provider.DropTokenCodecProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.tictac.droptoken.data.impl.GameDaoImpl;
import com.tictac.droptoken.data.impl.GameStatusDaoImpl;
import com.tictac.droptoken.data.impl.MoveDaoImpl;
import com.tictac.droptoken.data.impl.PlayerDaoImpl;
import io.dropwizard.Application;
import io.dropwizard.jersey.errors.EarlyEofExceptionMapper;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import ru.vyarus.dropwizard.guice.GuiceBundle;

import java.util.ArrayList;
import java.util.List;


import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 *
 */
public class DropTokenApplication extends Application<DropTokenConfiguration> {

    private static final String DATABASE = "droptoken";
    private static final String DB_CONNECTION_STRING = "mongodb://localhost:27017";

    public static void main(String[] args) throws Exception {
        new DropTokenApplication().run(args);
    }

    @Override
    public String getName() {
        return "Drop Token";
    }

    @Override
    public void initialize(Bootstrap<DropTokenConfiguration> bootstrap) {
        DropTokenModule dropTokenModule = new DropTokenModule();

        bootstrap.addBundle(GuiceBundle.builder()
                .enableAutoConfig(getClass().getPackage().getName())
                .modules(dropTokenModule)
                .build());
    }

    @Override
    public void run(DropTokenConfiguration configuration,
                    Environment environment) {
        environment.getObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new Jdk8Module());
        environment.getObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        environment.jersey().register(new DropTokenExceptionMapper());
        environment.jersey().register(new WebApplicationExceptionMapper());
        environment.jersey().register(new JerseyViolationExceptionMapper());
        environment.jersey().register(new JsonProcessingExceptionMapper());
        environment.jersey().register(new EarlyEofExceptionMapper());

    }

}
