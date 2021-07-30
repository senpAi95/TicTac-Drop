package com.tictac.droptoken.validator;

import com.tictac.droptoken.model.GameStatus;

import javax.ws.rs.WebApplicationException;

import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.GAME_NOT_FOUND;

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
