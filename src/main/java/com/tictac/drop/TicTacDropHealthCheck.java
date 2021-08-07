package com.tictac.drop;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

import javax.inject.Inject;

/**
 * Health check for the webservice. Its dependent on DB availability.
 */
public class TicTacDropHealthCheck extends NamedHealthCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicTacDropHealthCheck.class);

    MongoDatabase database;

    @Inject
    public TicTacDropHealthCheck(MongoDatabase database) {
        this.database = database;
    }

    @Override
    protected Result check() {
        try {
            MongoIterable<String> allCollections = database.listCollectionNames();
            for (String collection : allCollections) {
                // iterating tries to establish a MongoDb connection and failing will throw Exception
            }
        } catch (Exception e) {
            LOGGER.error("mongo db down: {}", e.getMessage());
            return Result.unhealthy("Couldn't list collections");
        }
        return Result.healthy();
    }

    @Override
    public String getName() {
        return "Drop Token";
    }
}
