package com.tictac.drop.data;

import com.tictac.drop.model.GameStatus;
import com.mongodb.MongoException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * GameStatusDao for interacting with DB for read and write operations.
 */
public interface GameStatusDao {

    /**
     * Creates {@link GameStatus} in the {@link GameStatus} MongoCollection.
     *
     * @param gameId
     * @param playerIds
     * @throws MongoException
     */
    void createGameStatus(@Nonnull final String gameId, List<String> playerIds) throws MongoException;

    /**
     * Retrieves {@link GameStatus} from the {@link GameStatus} MongoCollection.
     *
     * @param gameId
     * @return
     */
    Optional<GameStatus> getGameStatus(@Nonnull final String gameId);

    /**
     * Retrieves all in progress games from the {@link GameStatus} MongoCollection.
     *
     * @return
     */
    List<String> getInProgressGames();

    /**
     * Updates the game status in the {@link GameStatus} MongoCollection.
     *
     * @param gameId
     * @param name
     * @throws MongoException
     */
    void updateGameStatusToCompleted(@Nonnull final String gameId, @Nonnull final String name) throws MongoException;
}
