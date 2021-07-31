package com.tictac.droptoken;

import com.tictac.droptoken.data.GameDao;
import com.tictac.droptoken.data.GameStatusDao;
import com.tictac.droptoken.data.MoveDao;
import com.tictac.droptoken.data.PlayerDao;
import com.tictac.droptoken.model.CreateGameRequest;
import com.tictac.droptoken.model.GameStatus;
import com.tictac.droptoken.validator.GameStatusValidator;
import com.tictac.droptoken.validator.NewGameValidator;
import com.tictac.droptoken.validator.PlayerQuitValidator;
import com.tictac.droptoken.validator.PostMoveValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DropTokenServiceTest {

    private DropTokenService dropTokenService;
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

        dropTokenService = new DropTokenService(
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
        dropTokenService.getInProgressGames();
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

        dropTokenService.createNewGame(createGameRequestMock);

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

        dropTokenService.getGameStatus(gameId);

        verify(gameStatusDaoMock).getGameStatus(eq(gameId));
        verify(gameStatus).getPlayerIds();
        verify(gameStatusValidatorMock).validate(gameStatus);
        verify(playerDaoMock).getPlayerNamesByIds(playerIds);
        verify(gameStatus).getStatus();
    }

    @Test
    public void testPostMove() {

    }
}