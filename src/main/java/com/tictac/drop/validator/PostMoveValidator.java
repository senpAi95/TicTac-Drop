package com.tictac.drop.validator;

import com.tictac.drop.model.Game;
import com.tictac.drop.model.GameStatus;
import com.tictac.drop.model.Player;

import javax.ws.rs.WebApplicationException;

import static com.tictac.drop.util.ExceptionStatusCodeAndMessage.INVALID_PLAYER_GAME_TURN;

/**
 * A validator that validates a request for requests related to posting a move.
 */
public class PostMoveValidator extends MoveValidator{

    void validatePlayerTurn(Game game, String playerId) throws WebApplicationException{
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
