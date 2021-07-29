package com.tictac.droptoken.data;

import com.tictac.droptoken.model.Player;
import com.mongodb.MongoException;

import java.util.List;
import java.util.Optional;

public interface PlayerDao {

    /**
     * Adds a list of players to the {@link Player} MongoCollection.
     *
     * @param names names of players.
     * @return list of playerIds for the given player names after successfully persisting the data.
     * @throws MongoException
     */
    List<String> addPlayers(List<String> names) throws MongoException;

    /**
     * Retrieves a {@link Player} by playerId from the {@link Player} MongoCollection.
     *
     * @param playerId
     * @return
     */
    Optional<Player> getPlayer(String playerId);

    /**
     * Retrieves a list of all {@link Player}s from the {@link Player} MongoCollection.
     *
     * @param playerIds
     * @return
     */
    List<Player> getPlayers(List<String> playerIds);

    /**
     * Retrieves {@link Player} names associated to the playerIds from the {@link Player} MongoCollection.
     *
     * @param playerIds
     * @return
     */
    List<String> getPlayerNamesByIds(List<String> playerIds);
}
