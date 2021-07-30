package com.tictac.droptoken.validator;

import com.tictac.droptoken.model.Game;
import com.tictac.droptoken.model.Move;

import javax.ws.rs.WebApplicationException;

import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.GAME_NOT_FOUND;
import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.MOVE_NOT_FOUND;

public class MoveByMoveIdValidator {

    void validateGame(Game game) throws WebApplicationException{
        if(game == null) {
            throw new WebApplicationException(GAME_NOT_FOUND.getMessage(), GAME_NOT_FOUND.getStatusCode());
        }
    }

    void validateMove(Move move) {
        if(move == null) {
            throw new WebApplicationException(MOVE_NOT_FOUND.getMessage(), MOVE_NOT_FOUND.getStatusCode());
        }
    }

    public void validate(Game game, Move move) {
        validateGame(game);
        validateMove(move);
    }
}
