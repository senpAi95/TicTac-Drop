package com.tictac.drop.validator;

import com.tictac.drop.model.GameStatus;

import javax.ws.rs.WebApplicationException;

import static com.tictac.drop.util.ExceptionStatusCodeAndMessage.GAME_NOT_FOUND;

/**
 * A validator that validates a request for retrieving gamestatus.
 */
public class GameStatusValidator {
    void validateGameIfExists(GameStatus gameStatus) throws WebApplicationException{
        if(gameStatus == null) {
            throw new WebApplicationException(GAME_NOT_FOUND.getMessage(), GAME_NOT_FOUND.getStatusCode());
        }
    }

    public void validate(GameStatus gameStatus) throws WebApplicationException{
        validateGameIfExists(gameStatus);
    }
}
