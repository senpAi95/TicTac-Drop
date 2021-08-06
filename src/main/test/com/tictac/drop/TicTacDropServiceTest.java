package com.tictac.drop;

import com.tictac.drop.data.GameDao;
import com.tictac.drop.data.GameStatusDao;
import com.tictac.drop.data.MoveDao;
import com.tictac.drop.data.PlayerDao;
import com.tictac.drop.model.CreateGameRequest;
import com.tictac.drop.model.Game;
import com.tictac.drop.model.GameStatus;
import com.tictac.drop.model.Player;
import com.tictac.drop.validator.GameStatusValidator;
import com.tictac.drop.validator.NewGameValidator;
import com.tictac.drop.validator.PlayerQuitValidator;
import com.tictac.drop.validator.PostMoveValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TicTacDropServiceTest {

    private TicTacDropService ticTacDropService;
    private GameDao gameDaoMock;
    private MoveDao moveDaoMock;
    private PlayerDao playerDaoMock;
    private GameStatusDao gameStatusDaoMock;

    private GridOperations gridOperationsMock;
    private NewGameValidator newGameValidatorMock;
    private GameStatusValidator gameStatusValidatorMock;
    private PostMoveValidator postMoveValidatorMock;
    private PlayerQuitValidator playerQuitValidatorMock;

    @BeforeEach
    public void beforeAll() {
        gameDaoMock = mock(GameDao.class);
        moveDaoMock = mock(MoveDao.class);
        playerDaoMock = mock(PlayerDao.class);
        gameStatusDaoMock = mock(GameStatusDao.class);
        gridOperationsMock = mock(GridOperations.class);
        newGameValidatorMock = mock(NewGameValidator.class);
        gameStatusValidatorMock = mock(GameStatusValidator.class);
        postMoveValidatorMock = mock(PostMoveValidator.class);
        playerQuitValidatorMock = mock(PlayerQuitValidator.class);

        ticTacDropService = new TicTacDropService(
                gameDaoMock,
                gameStatusDaoMock,
                moveDaoMock,
                playerDaoMock,
                gridOperationsMock,
                newGameValidatorMock,
                gameStatusValidatorMock,
                postMoveValidatorMock,
                playerQuitValidatorMock);
    }

    @AfterEach
    void tearDown() {
        gameDaoMock = null;
        gameStatusDaoMock = null;
        moveDaoMock = null;
        playerDaoMock = null;
        gridOperationsMock = null;
        newGameValidatorMock = null;
        gameStatusValidatorMock = null;
        postMoveValidatorMock = null;
        playerQuitValidatorMock = null;
    }

    @Test
    public void testGetInProgressGame() {
        List<String> gameIds = emptyList();
        when(gameStatusDaoMock.getInProgressGames()).thenReturn(gameIds);
        ticTacDropService.getInProgressGames();
        verify(gameStatusDaoMock).getInProgressGames();
    }

    @Test
    public void testCreateNewGame() {
        CreateGameRequest createGameRequestMock = mock(CreateGameRequest.class);
        List<String> players = mock(List.class);
        List<String> playerIds = mock(List.class);
        String gameId = "gameId";
        doNothing().when(newGameValidatorMock).validate(createGameRequestMock);
        when(createGameRequestMock.getPlayers()).thenReturn(players);
        when(playerDaoMock.addPlayers(players)).thenReturn(playerIds);
        when(gameDaoMock.createGame(createGameRequestMock, playerIds)).thenReturn(gameId);
        doNothing().when(gameStatusDaoMock).createGameStatus(gameId, playerIds);

        ticTacDropService.createNewGame(createGameRequestMock);

        verify(newGameValidatorMock).validate(createGameRequestMock);
        verify(createGameRequestMock).getPlayers();
        verify(playerDaoMock).addPlayers(players);
        verify(gameDaoMock).createGame(createGameRequestMock, playerIds);
        verify(gameStatusDaoMock).createGameStatus(gameId, playerIds);
    }

    @Test
    public void testGetGameStatus() {
        String gameId = "gameId";
        String state = "state";
        List<String> playerIds = mock(List.class);
        List<String> playerNames = mock(List.class);
        GameStatus gameStatus = mock(GameStatus.class);

        when(gameStatusDaoMock.getGameStatus(eq(gameId))).thenReturn(Optional.of(gameStatus));
        doNothing().when(gameStatusValidatorMock).validate(gameStatus);
        when(gameStatus.getPlayerIds()).thenReturn(playerIds);
        when(playerDaoMock.getPlayerNamesByIds(playerIds)).thenReturn(playerNames);
        when(gameStatus.getStatus()).thenReturn(state);

        ticTacDropService.getGameStatus(gameId);

        verify(gameStatusDaoMock).getGameStatus(eq(gameId));
        verify(gameStatus).getPlayerIds();
        verify(gameStatusValidatorMock).validate(gameStatus);
        verify(playerDaoMock).getPlayerNamesByIds(playerIds);
        verify(gameStatus).getStatus();
    }

    @Test
    public void testPostMove() {
        String gameId = "gameId";
        Game game = mock(Game.class);
        GameStatus gameStatus = mock(GameStatus.class);
        Player player  = mock(Player.class);
        String playerId = "playerId";
        String nextPlayerId = "nextPlayerId";
        String[] grid =
                { "0","0","0","0",
                  "0","0","0","0",
                  "0","0","0","0",
                  "0","0","0","0"
                };
        List<String> playerIdsInGame = emptyList();
        int gridLength = 4;

        when(gameDaoMock.getGame(eq(gameId))).thenReturn(Optional.of(game));
        when(gameStatusDaoMock.getGameStatus(eq(gameId))).thenReturn(Optional.of(gameStatus));
        when(playerDaoMock.getPlayer(eq(playerId))).thenReturn(Optional.of(player));
        doNothing().when(postMoveValidatorMock).validate(game, player, gameStatus, playerId);
        when(game.getGridValues()).thenReturn(grid);
        doNothing().when(moveDaoMock).addMove(any());
        when(game.getPlayerIds()).thenReturn(playerIdsInGame);

        when(game.nextPlayerInGame(game.getPlayerIds(), playerId)).thenReturn(Optional.of(nextPlayerId));
        when(game.getLength()).thenReturn(gridLength);
        doNothing().when(gameDaoMock).addMove(eq(gameId), any(), any(), eq(nextPlayerId));

        ticTacDropService.postMove(gameId, playerId, 2);

        verify(gameDaoMock).getGame(eq(gameId));
        verify(gameStatusDaoMock).getGameStatus(eq(gameId));
        verify(playerDaoMock).getPlayer(eq(playerId));
        verify(postMoveValidatorMock).validate(game, player, gameStatus, playerId);
        verify(game).getGridValues();
        verify(moveDaoMock).addMove(any());
        verify(game, times(2)).getPlayerIds();
        verify(game, times(2)).nextPlayerInGame(game.getPlayerIds(), playerId);
        verify(game).getLength();
        verify(gameDaoMock).addMove(eq(gameId), any(), any(), eq(nextPlayerId));
    }
}