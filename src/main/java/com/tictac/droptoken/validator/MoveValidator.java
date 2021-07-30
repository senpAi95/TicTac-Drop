package com.tictac.droptoken.validator;

import com.tictac.droptoken.model.Game;
import com.tictac.droptoken.model.GameStatus;
import com.tictac.droptoken.model.Player;
import com.tictac.droptoken.util.ExceptionStatusCodeAndMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;

import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.GAME_COMPLETED;
import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.GAME_NOT_FOUND;
import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.PLAYER_NOT_FOUND;
import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.PLAYER_NOT_IN_GAME;

public abstract class MoveValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveValidator.class);
    private static final String GAME_STATUS_DONE = "DONE";

    void validateGame(Game game) throws WebApplicationException {
        if(game == null) {
            LOGGER.error("Unable to find game for id {}", game.getId());
            throwException(GAME_NOT_FOUND);
        }
    }

    void validateGameStatus(GameStatus gameStatus) throws WebApplicationException {
        if(gameStatus == null) {
            LOGGER.error("Unable to find gameStatus for id {}", gameStatus.getId());
            throwException(GAME_NOT_FOUND);
        }
    }

    void validatePlayer(Player player, String playerId) throws WebApplicationException {
        if(player == null) {
            LOGGER.error("PlayerId {} doesn't exist", playerId);
            throwException(PLAYER_NOT_FOUND);
        }
    }

    void validatePlayerInGame(Game game, String playerId) throws WebApplicationException{
        if(!game.getPlayerIds().contains(playerId)) {
            LOGGER.error("PlayerId {} doesn't belong to gameId {}", playerId, game.getId());
            throwException(PLAYER_NOT_IN_GAME);
        }
    }

    void validateGameState(GameStatus gameStatus) {
        if(gameStatus.getStatus().equals(GAME_STATUS_DONE)) {
            throwException(GAME_COMPLETED);
        }
    }

    void throwException(ExceptionStatusCodeAndMessage exceptionStatusCodeAndMessage) throws WebApplicationException{
        throw new WebApplicationException(exceptionStatusCodeAndMessage.getMessage(), exceptionStatusCodeAndMessage.getStatusCode());
    }
}
