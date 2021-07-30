package com.tictac.droptoken.validator;

import com.tictac.droptoken.model.Game;
import com.tictac.droptoken.model.GameStatus;
import com.tictac.droptoken.model.Player;

import javax.ws.rs.WebApplicationException;

import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.INVALID_PLAYER_GAME_TURN;

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
