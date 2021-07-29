package com.tictac.droptoken.data.impl;

import com.tictac.droptoken.CollectionNames;
import com.tictac.droptoken.data.CollectionDao;
import com.tictac.droptoken.data.GameDao;
import com.tictac.droptoken.model.CreateGameRequest;
import com.tictac.droptoken.model.Game;
import com.tictac.droptoken.util.ExceptionStatusCodeAndMessage;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.tictac.droptoken.util.UidGenerator.generateUid;

public class GameDaoImpl extends CollectionDao<Game> implements GameDao {

    private static final String OBJECT_ID = "_id";
    private static final String GRID_VALUES_KEY = "gridValues";
    private static final String MOVE_IDS_KEY = "moveIds";
    private static final String NEXT_PLAYER_ID = "nextPlayerTurnId";
    private static final String PLAYERS_KEY = "players";

    public GameDaoImpl(MongoDatabase database) {
        super(database, CollectionNames.GAME.getValue(), Game.class);
    }

    @Override
    public String createGame(CreateGameRequest request, List<String> playerIds) throws MongoException {
        List<String> distinctSortedPlayers = playerIds.stream().distinct().sorted().collect(Collectors.toList());
        // can same players play multiple games in parallel? if so, append timestamp to sortedPlayers.
        final String gameId = generateUid(distinctSortedPlayers);
        final Game game = new Game(gameId, playerIds, request.getColumns());
        mongoCollection.insertOne( game);
        return gameId;
    }

    @Override
    public Optional<Game> getGame(String gameId) {
        Optional<Game> game = Optional.ofNullable(mongoCollection.find(eq(OBJECT_ID, gameId), Game.class).first());
        return game;
    }

    @Override
    public void addQuitMove(String gameId, String moveId, Optional<String> playerId) {
        addMove(gameId, moveId, null, playerId);
    }

    @Override
    public void addMove(String gameId, String moveId, List<String> grid, Optional<String> nextPlayerId) {
        Bson update = Updates.push(MOVE_IDS_KEY, moveId);
        if(grid != null)
        {
            Iterable<String> iterable = () -> grid.iterator();
            update = and(update, Updates.set(GRID_VALUES_KEY, iterable));
        }
        if(!nextPlayerId.isEmpty()) {
            update = and(update, Updates.set(NEXT_PLAYER_ID, nextPlayerId.get()));
        }

        mongoCollection.updateOne(eq(OBJECT_ID, gameId), update);
    }
    @Override
    public void removePlayer(String gameId, String playerId) {
        mongoCollection.updateOne(eq(OBJECT_ID, gameId), Updates.pull(PLAYERS_KEY, playerId));
    }

    @Override
    public List<String> getMoves(String gameId, int offset, int limit) throws IllegalArgumentException{
        List<String> moves = new ArrayList<>();
        Optional<Game> game = Optional.ofNullable(mongoCollection.find(eq(OBJECT_ID, gameId),
                Game.class).first());
        // optimize if possible?
        if(game.isEmpty()) {
            throw new IllegalArgumentException(ExceptionStatusCodeAndMessage.GAME_NOT_FOUND.getMessage());
        }
        moves.addAll(game.get().getMoveIds());

        return moves.stream().skip(offset)
                .limit(limit).collect(Collectors.toList());
//        return moves;
    }
}
