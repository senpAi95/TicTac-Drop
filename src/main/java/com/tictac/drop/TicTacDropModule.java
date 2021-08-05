package com.tictac.drop;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.tictac.drop.codec.provider.DropTokenCodecProvider;
import com.tictac.drop.data.GameDao;
import com.tictac.drop.data.GameStatusDao;
import com.tictac.drop.data.MoveDao;
import com.tictac.drop.data.PlayerDao;
import com.tictac.drop.data.impl.GameDaoImpl;
import com.tictac.drop.data.impl.GameStatusDaoImpl;
import com.tictac.drop.data.impl.MoveDaoImpl;
import com.tictac.drop.data.impl.PlayerDaoImpl;
import com.tictac.drop.validator.GameStatusValidator;
import com.tictac.drop.validator.MoveByMoveIdValidator;
import com.tictac.drop.validator.NewGameValidator;
import com.tictac.drop.validator.PlayerQuitValidator;
import com.tictac.drop.validator.PostMoveValidator;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import javax.inject.Named;
import java.util.Collections;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class TicTacDropModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GridOperations.class);
        bind(GameDao.class).to(GameDaoImpl.class).asEagerSingleton();
        bind(GameStatusDao.class).to(GameStatusDaoImpl.class);
        bind(PlayerDao.class).to(PlayerDaoImpl.class);
        bind(MoveDao.class).to(MoveDaoImpl.class);
        bind(TicTacDropService.class);


        bind(GameStatusValidator.class);
        bind(MoveByMoveIdValidator.class);
        bind(NewGameValidator.class);
        bind(PostMoveValidator.class);
        bind(PlayerQuitValidator.class);
    }


    @Provides
    @Singleton
    public MongoDatabase getDatabase(TicTacDropConfiguration configuration) {
        final MongoClient mongoClient = MongoClients.create(configuration.getDbConnectionString());

        CodecRegistry pojoCodecRegistry = fromRegistries(
                CodecRegistries.fromProviders(Collections.singletonList(new DropTokenCodecProvider())),
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        final MongoDatabase database = mongoClient.getDatabase(configuration.getDatabaseName())
                .withCodecRegistry(pojoCodecRegistry);
        return database;
    }


    @Provides
    @Named("minPlayers")
    public int getMinPlayers(TicTacDropConfiguration configuration) {
        return Integer.valueOf(configuration.getMinPlayers());
    }

    @Provides
    @Named("minGridLength")
    public int getMinGridLength(TicTacDropConfiguration configuration) {
        return Integer.valueOf(configuration.getMinGridLength());
    }
}
