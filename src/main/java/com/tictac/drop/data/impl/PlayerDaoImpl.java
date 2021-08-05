package com.tictac.drop.data.impl;

import com.tictac.drop.CollectionNames;
import com.tictac.drop.data.CollectionDao;
import com.tictac.drop.data.PlayerDao;
import com.tictac.drop.model.Player;
import com.tictac.drop.util.UidGenerator;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

public class PlayerDaoImpl extends CollectionDao<Player> implements PlayerDao {

    private static final String OBJECT_ID = "_id";

    @Inject
    public PlayerDaoImpl(MongoDatabase database) {
        // if collection name is not valid, its not throwing error while application is running.
        super(database, CollectionNames.PLAYERS.getValue(), Player.class);
    }

    public String addPlayer(String name) {
        String playerId = UidGenerator.generateUid(name);
        Optional<Player> optionalPlayer = getPlayer(playerId);
        if(optionalPlayer.isEmpty()) {
            Player player = new Player();
            player.setName(name);
            player.setId(playerId);
            mongoCollection.insertOne(player);
        }
        return playerId;
    }

    @Override
    public List<String> addPlayers(List<String> names) throws MongoException {
        // optimize below to bulk operations instead of one by one
        return names.stream().map(name -> addPlayer(name)).collect(Collectors.toList());
    }
    @Override
    public Optional<Player> getPlayer(String playerId) {
        Optional<Player> player = Optional.ofNullable(mongoCollection.find(eq(OBJECT_ID, playerId), Player.class).first());
        return player;
    }

    @Override
    public List<String> getPlayerNamesByIds(List<String> playerIds) {
        return getPlayers(playerIds).stream().map(player -> player.getName()).collect(Collectors.toList());
    }

    @Override
    @Nonnull
    public List<Player> getPlayers(@Nonnull List<String> playerIds) {

        Iterable<String> iterable = () -> playerIds.iterator();
        MongoCursor<Player> playerIterable = mongoCollection.find(in(OBJECT_ID,iterable), Player.class).iterator();
        List<Player> players = new ArrayList<>();
        while(playerIterable.hasNext()) {
            players.add(playerIterable.next());
        }
        return players;
    }
}
