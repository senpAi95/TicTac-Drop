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
import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.INVALID_PLAYER_GAME_TURN;
import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.PLAYER_NOT_FOUND;
import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.PLAYER_NOT_IN_GAME;

public class PostMoveValidator extends MoveValidator{

    void validatePlayerTurn(Game game, String playerId) {
        if(!game.getNextPlayerTurnId().equals(playerId)) {
            throwException(INVALID_PLAYER_GAME_TURN);
        }
    }

    public void validate(Game game, Player player, GameStatus gameStatus, String playerId) throws WebApplicationException {
        validateGame(game);
        validateGameStatus(gameStatus);
        validatePlayer(player, playerId);
        validatePlayerInGame(game, playerId);
        validateGameState(gameStatus);
        validatePlayerTurn(game, playerId);
    }


}
