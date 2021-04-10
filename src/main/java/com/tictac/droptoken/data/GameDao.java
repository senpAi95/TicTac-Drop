package com.tictac.droptoken.data;

import com.tictac.droptoken.model.CreateGameRequest;
import com.tictac.droptoken.model.Game;

import java.util.List;
import java.util.Optional;

public interface GameDao {

    /**
     * Creates a {@link Game} in the {@link Game} Mongo Collection.
     *
     * @param gameId
     * @param request
     * @param playerIds
     */
    void createGame(String gameId, CreateGameRequest request, List<String> playerIds);

    /**
     * Retrieves a {@link Game} from the {@link Game} Mongo Collection.
     *
     * @param gameId
     * @return
     */
    Optional<Game> getGame(String gameId);

    /**
     * Adds a {@link Move} id to game in the {@link Game} Mongo Collection.
     *
     * @param gameId
     * @param moveId
     * @param grid
     * @param nexPlayer
     */
    void addMove(String gameId, String moveId, List<String> grid, Optional<String> nexPlayer);

    /**
     * Removes a {@link Player} id from game in the {@link Game} Mongo Collection.
     *
     * @param gameId
     * @param playerId
     */
    void removePlayer(String gameId, String playerId);

    /**
     * Retrieves all moves within limit from offset in the {@link Game} Mongo Collection.
     *
     * @param gameId
     * @param offset
     * @param limit
     * @return
     * @throws IllegalArgumentException when given gameId is not valid.
     */
    List<String> getMoves(String gameId, int offset, int limit) throws IllegalArgumentException;

    /**
     * Adds a quit move to game in the {@link Game} Mongo Collection.
     *
     * @param gameId
     * @param id
     * @param nextPlayer
     */
    void addQuitMove(String gameId, String id, Optional<String> nextPlayer);
}
