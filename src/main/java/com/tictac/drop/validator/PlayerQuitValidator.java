package com.tictac.drop.validator;

import com.tictac.drop.model.Game;
import com.tictac.drop.model.GameStatus;
import com.tictac.drop.model.Player;

import javax.ws.rs.WebApplicationException;

/**
 * A validator that validates a request for requests related to quit move.
 */
public class PlayerQuitValidator extends MoveValidator{
    public void validate(Game game, Player player, GameStatus gameStatus, String playerId) throws WebApplicationException {
        validateGame(game);
        validateGameStatus(gameStatus);
        validatePlayer(player, playerId);
        validatePlayerInGame(game, playerId);
        validateGameState(gameStatus);
    }
}
