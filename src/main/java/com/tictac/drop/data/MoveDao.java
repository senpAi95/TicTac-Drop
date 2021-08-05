package com.tictac.drop.data;

import com.tictac.drop.model.Move;

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
    void addMove(Move move);

    /**
     * Retrieves all {@link Move}s by moveIds from the {@link Move} MongoCollection.
     *
     * @param moveIds
     * @return
     */
    List<Move> getMoves(List<String> moveIds);
}