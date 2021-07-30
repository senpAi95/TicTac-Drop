package com.tictac.droptoken.data.impl;

import com.tictac.droptoken.CollectionNames;
import com.tictac.droptoken.data.CollectionDao;
import com.tictac.droptoken.data.MoveDao;
import com.tictac.droptoken.model.Move;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

public class MoveDaoImpl extends CollectionDao<Move> implements MoveDao {

    private static final String OBJECT_ID = "_id";

    @Inject
    public MoveDaoImpl(MongoDatabase database) {
        // if collection name is not valid, its not throwing error while application is running.
        super(database, CollectionNames.MOVES.getValue(), Move.class);
    }

    @Override
    public Optional<Move> getMove(String moveId) {
        Optional<Move> move = Optional.ofNullable(mongoCollection.find(eq(OBJECT_ID, moveId),
                Move.class).first());
        return move;
    }

    @Override
    public void createMove(Move move) {
        mongoCollection.insertOne(move);
    }

    public List<Move> getMoves(List<String> moveIds) {
        List<Move> moves = new ArrayList<>();
        Iterable iterable = () -> moveIds.iterator();
        MongoCursor<Move> moveMongoCursor =  mongoCollection.find(in(OBJECT_ID, iterable), Move.class).iterator();
        while(moveMongoCursor.hasNext()) {
            moves.add(moveMongoCursor.next());
        }
        return moves;
    }
}
