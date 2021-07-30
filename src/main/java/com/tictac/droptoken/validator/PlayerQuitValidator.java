package com.tictac.droptoken.validator;

import com.tictac.droptoken.model.Game;
import com.tictac.droptoken.model.GameStatus;
import com.tictac.droptoken.model.Player;

import javax.ws.rs.WebApplicationException;

public class PlayerQuitValidator extends MoveValidator{
    public void validate(Game game, Player player, GameStatus gameStatus, String playerId) throws WebApplicationException {
        validateGame(game);
        validateGameStatus(gameStatus);
        validatePlayer(player, playerId);
        validatePlayerInGame(game, playerId);
        validateGameState(gameStatus);
    }
}
