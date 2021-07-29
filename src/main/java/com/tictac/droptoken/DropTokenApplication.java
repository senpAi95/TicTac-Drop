package com.tictac.droptoken;

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

        environment.jersey().register(new DropTokenExceptionMapper());
        environment.jersey().register(new WebApplicationExceptionMapper());
        environment.jersey().register(new JerseyViolationExceptionMapper());
        environment.jersey().register(new JsonProcessingExceptionMapper());
        environment.jersey().register(new EarlyEofExceptionMapper());

        final MongoClient mongoClient = MongoClients.create(DB_CONNECTION_STRING);
        List<CodecProvider> providerList = new ArrayList<>();
        providerList.add(new DropTokenCodecProvider());


        CodecRegistry pojoCodecRegistry = fromRegistries(CodecRegistries.fromProviders(providerList),MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
                );
        final MongoDatabase database = mongoClient.getDatabase(DATABASE)
                .withCodecRegistry(pojoCodecRegistry);
        DropTokenService dropTokenService = new DropTokenService(
                new GameDaoImpl(database),
                new GameStatusDaoImpl(database),
                new MoveDaoImpl(database),
                new PlayerDaoImpl(database));

        final DropTokenResource resource = new DropTokenResource(dropTokenService);
        environment.jersey().register(resource);

    }

}
