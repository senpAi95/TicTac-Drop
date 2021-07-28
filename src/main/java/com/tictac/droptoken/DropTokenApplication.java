package com.tictac.droptoken;

import com.tictac.droptoken.codec.provider.DropTokenCodecProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.tictac.droptoken.inject.DependencyInjectionBundle;
import com.tictac.droptoken.inject.DropTokenConfiguration;
import com.tictac.droptoken.inject.NamedProperty;
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
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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
        return "98Point6 - Drop Token";
    }

    @Override
    public void initialize(Bootstrap<DropTokenConfiguration> bootstrap) {
    }

    @Override
    public void run(DropTokenConfiguration configuration,
                    Environment environment) {

        environment.getObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new Jdk8Module());
        environment.getObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


        Consumer<Object> environmentRegistrar = __ -> environment.jersey().register(__);
        environmentRegistrar.accept(new DropTokenApplication());
        environmentRegistrar.accept(new DropTokenExceptionMapper());
        environmentRegistrar.accept(new WebApplicationExceptionMapper());
        environmentRegistrar.accept(new JerseyViolationExceptionMapper());
        environmentRegistrar.accept(new JsonProcessingExceptionMapper());
        environmentRegistrar.accept(new EarlyEofExceptionMapper());


//         DependencyInjectionBundle dependencyInjectionBundle = new DependencyInjectionBundle();
//         dependencyInjectionBundle.run(configuration, environment);

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

        final MongoClient mongoClient = MongoClients.create(DB_CONNECTION_STRING);
        List<CodecProvider> providerList = new ArrayList<>();
        providerList.add(new DropTokenCodecProvider());


        CodecRegistry pojoCodecRegistry = fromRegistries(CodecRegistries.fromProviders(providerList),MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
                );
        final MongoDatabase database = mongoClient.getDatabase(DATABASE)
                .withCodecRegistry(pojoCodecRegistry);
        DropTokenService dropTokenService = new DropTokenService(database);

        final DropTokenResource resource = new DropTokenResource(dropTokenService);
        environmentRegistrar.accept(resource);

    }

}
