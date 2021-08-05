package com.tictac.drop.data.impl;

import com.tictac.drop.CollectionNames;
import com.tictac.drop.data.CollectionDao;
import com.tictac.drop.data.GameStatusDao;
import com.tictac.drop.model.GameStatus;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static java.util.stream.Collectors.toList;

public class GameStatusDaoImpl extends CollectionDao<GameStatus> implements GameStatusDao {

    private static final String OBJECT_ID = "_id";
    private static final String STATUS_KEY = "status";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_DONE = "DONE";
    private static final String WINNER_KEY = "winner";

    @Inject
    public GameStatusDaoImpl(@Nonnull MongoDatabase database) {
        super(database, CollectionNames.STATUS.getValue(), GameStatus.class);
    }

    @Override
    public void createGameStatus(@Nonnull final String gameId, @Nonnull List<String> playerIds) throws MongoException {
        GameStatus gameStatus = new GameStatus();
        gameStatus.setId(gameId);
        gameStatus.setStatus(STATUS_IN_PROGRESS);
        gameStatus.setPlayerIds(playerIds);
        mongoCollection.insertOne(gameStatus);
    }

    @Override
    @Nonnull
    public Optional<GameStatus> getGameStatus(@Nonnull final String gameId){
        final Optional<GameStatus> gameStatus = Optional.ofNullable(mongoCollection.find(eq(OBJECT_ID, gameId), GameStatus.class).first());
        return gameStatus;
    }

    @Override
    @Nonnull
    public List<String> getInProgressGames() {
        ArrayList<GameStatus> gameStatuses = mongoCollection.find(eq(STATUS_KEY, STATUS_IN_PROGRESS), GameStatus.class)
                .into(new ArrayList<>());

        return gameStatuses.stream().map(gameStatus -> gameStatus.getId()).collect(toList());
    }

    public void updateGameStatusToCompleted(@Nonnull final String gameId,@Nonnull final String name) {
        mongoCollection.updateOne(eq(OBJECT_ID, gameId),
                and(Updates.set(STATUS_KEY, STATUS_DONE), Updates.set(WINNER_KEY, name)));
    }

}
