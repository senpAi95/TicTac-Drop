package com.tictac.droptoken.data;

import com.tictac.droptoken.model.Move;

import java.util.List;
import java.util.Optional;

public interface MoveDao {

    /**
     * Retrieves {@link Move} by moveId from the {@link Move} MongoCollection.
     *
     * @param moveId
     * @return
     */
    Optional<Move> getMove(String moveId);

    /**
     * Creates a {@link Move} in the MongoCollection.
     *
     * @param move
     */
    void createMove(Move move);

    /**
     * Retrieves all {@link Move}s by moveIds from the {@link Move} MongoCollection.
     *
     * @param moveIds
     * @return
     */
    List<Move> getMoves(List<String> moveIds);
}
