package com.tictac.drop;

import com.mongodb.MongoException;
import com.tictac.drop.data.*;
import com.tictac.drop.model.*;
import com.tictac.drop.util.ExceptionStatusCodeAndMessage;
import com.tictac.drop.validator.GameStatusValidator;
import com.tictac.drop.validator.MoveByMoveIdValidator;
import com.tictac.drop.validator.NewGameValidator;
import com.tictac.drop.validator.PlayerQuitValidator;
import com.tictac.drop.validator.PostMoveValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tictac.drop.util.ExceptionStatusCodeAndMessage.GAME_NOT_FOUND;
import static com.tictac.drop.util.ExceptionStatusCodeAndMessage.ILLEGAL_MOVE;
import static com.tictac.drop.util.ExceptionStatusCodeAndMessage.PROCESSING_EXCEPTION;

/**
 * A service which can handle API requests and delegate them to DAOs
 */
public class TicTacDropService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicTacDropService.class);
    private static final String MOVE = "MOVE";
    private static final String QUIT = "QUIT";

    private final GameDao gameDao;
    private final MoveDao moveDao;
    private final PlayerDao playerDao;
    private final GameStatusDao gameStatusDao;

    private final GridOperations gridOperations;
    private final NewGameValidator newGameValidator;
    private final GameStatusValidator gameStatusValidator;
    private final PostMoveValidator postMoveValidator;
    private final PlayerQuitValidator playerQuitValidator;

    @Inject
    public TicTacDropService(
            @Nonnull GameDao gameDao,
            @Nonnull GameStatusDao gameStatusDao,
            @Nonnull MoveDao moveDao,
            @Nonnull PlayerDao playerDao,
            @Nonnull GridOperations gridOperations,
            @Nonnull NewGameValidator newGameValidator,
            @Nonnull GameStatusValidator gameStatusValidator,
            @Nonnull PostMoveValidator postMoveValidator,
            @Nonnull PlayerQuitValidator playerQuitValidator) {
        this.gameDao = gameDao;
        this.moveDao = moveDao;
        this.playerDao = playerDao;
        this.gameStatusDao = gameStatusDao;
        this.gridOperations = gridOperations;
        this.newGameValidator = newGameValidator;
        this.gameStatusValidator = gameStatusValidator;
        this.postMoveValidator = postMoveValidator;
        this.playerQuitValidator = playerQuitValidator;
    }

    /**
     * Retrieves all IN_PROGRESS games.
     *
     * @return a list of gameIds
     */
    @Nonnull
    public List<String> getInProgressGames() {
        LOGGER.debug("Retrieving in progress games");
        List<String> gameIds = gameStatusDao.getInProgressGames();
        LOGGER.debug("Retrieved gameIds {}", gameIds);
        return gameIds;
    }

    /**
     * Creates a new game if player names are unique if below conditions are met.
     * <p>
     *     <ul>
     *         <li>Player names are unique.</li>
     *         <li>There should be minimum of 2 players.</li>
     *         <li>Should be a square grid.</li>
     *     </ul>
     * </p>
     *
     * @param request request object associated for creating a game.
     * @return {@code gameId} if a new game is created.
     */
    @Nonnull
    public String createNewGame(@Nonnull CreateGameRequest request) throws WebApplicationException{
        LOGGER.debug("Creating new game");

        newGameValidator.validate(request);

        String gameId = null;

        try {
            List<String> playerIds = playerDao.addPlayers(request.getPlayers());
            gameId = gameDao.createGame( request, playerIds);
            gameStatusDao.createGameStatus(gameId, playerIds);
        }  catch (MongoException e) {
            LOGGER.error("Unable to process the request because of {}", e);
            throwException(PROCESSING_EXCEPTION);
        }
        LOGGER.debug("Created gameId {}", gameId);
        return gameId;
    }

    /**
     * Retrieves the status of the game.
     *
     * @param gameId whose status needs to be retrieved.
     * @return {@link GameStatusResponse}
     */
    @Nonnull
    public GameStatusResponse getGameStatus(@Nonnull String gameId) {
        LOGGER.debug("Retrieving game status {}", gameId);
        GameStatusResponse.Builder builder = new GameStatusResponse.Builder();
        Optional<GameStatus> optionalGameStatus = gameStatusDao.getGameStatus(gameId);

        gameStatusValidator.validate(optionalGameStatus.orElse(null));

        GameStatus gameStatus = optionalGameStatus.get();

        if(gameStatus.getWinner() != null && (gameStatus.getWinner().length() != 0 || gameStatus.getStatus().equals("DONE"))) {
            builder.winner(gameStatus.getWinner());
        }

        List<String> playerNames = playerDao.getPlayerNamesByIds(gameStatus.getPlayerIds());

        return builder.players(playerNames).state(gameStatus.getStatus()).build();
    }

    /**
     * Posts a move for the game.
     *
     * @param gameId The id of {@link Game} for posting a move.
     * @param playerId The id of the {@link Player}
     * @param column The column where the token needs to be dropped.
     * @return {@link PostMoveResponse}
     */
    @Nonnull
    public PostMoveResponse postMove(@Nonnull String gameId, @Nonnull String playerId, int column) {
        Game game = gameDao.getGame(gameId).orElse(null);
        GameStatus gameStatus = gameStatusDao.getGameStatus(gameId).orElse(null);
        Player player = playerDao.getPlayer(playerId).orElse(null);

        postMoveValidator.validate(game, player, gameStatus, playerId);

        String[][] grid = buildGrid(game.getGridValues(), game.getLength());

        int rowIndex = gridOperations.getPossibleRow(grid, column);
        if(rowIndex == -1) {
            throwException(ILLEGAL_MOVE);
        }
        grid[rowIndex][column-1] = playerId;

        Move move = moveCreator(MOVE, player.getName(), column);


         if(gridOperations.winningMove(grid, playerId, rowIndex, column))
             gameStatusDao.updateGameStatusToCompleted(gameId, player.getName());

         //update gameDao, movesDao
        moveDao.addMove(move);
        Optional<String> nextPlayer =  game.nextPlayerInGame(game.getPlayerIds(), playerId);
        gameDao.addMove(gameId, move.getId(), getGridValues(grid), nextPlayer);

        String moveLink = createMoveLink(gameId, move.getId());

        return new PostMoveResponse.Builder().moveLink(moveLink).build();
    }

    @Nonnull
    private Move moveCreator(@Nonnull String type, @Nonnull String name, int column) {
        if(type.equals(QUIT)) {
            return new Move(type, name);
        }
        return new Move(type, name, column);
    }

    /**
     * Creates a move link.
     * @param gameId The id of the current game.
     * @param moveId The id of the current move.
     * @return
     */
    @Nonnull
    String createMoveLink(@Nonnull String gameId,@Nonnull String moveId) {
        final StringBuilder moveLink = new StringBuilder();
        moveLink.append(gameId);
        moveLink.append("/moves/");
        moveLink.append(moveId);
        return moveLink.toString();
    }

    /**
     * Performs the validation and posts a quit response and there by validating the state of the game.
     * <Note>
     *     If there are only 2 players in the game and if a player quits, remaining player will be the winner.
     * </Note>
     *
     * @param gameId The current gameId.
     * @param playerId The playerId who quits.
     */
    @Nonnull
    public void playerQuit(@Nonnull String gameId, @Nonnull String playerId) throws WebApplicationException {
        Game game = gameDao.getGame(gameId).orElse(null);
        Player player = playerDao.getPlayer(playerId).orElse(null);
        GameStatus gameStatus = gameStatusDao.getGameStatus(gameId).orElse(null);

        playerQuitValidator.validate(game, player, gameStatus, playerId);

        Move move = moveCreator(QUIT, player.getName(), -1);

        moveDao.addMove(move);

        try {
            List<String> inGamePlayerIds = game.getPlayerIds();
            Optional<String> nextPlayer = Optional.empty();
            gameDao.addQuitMove(gameId, move.getId(), nextPlayer);
            gameDao.removePlayer(gameId, playerId);
            inGamePlayerIds.remove(playerId);
            // check the number of players in the retrieved game is 2, if its 2 and a player quits. declare last one as winner
            if(inGamePlayerIds.size() == 1) {
                Optional<Player> winnerPlayer = playerDao.getPlayer(inGamePlayerIds.stream().findFirst().get());
                if(!winnerPlayer.isEmpty())
                    gameStatusDao.updateGameStatusToCompleted(gameId, winnerPlayer.get().getName());
            }

        } catch (MongoException  e) {
            throwException(PROCESSING_EXCEPTION);
        }

    }

    /**
     * Retrieves the moves of a game within the provide limit.
     *
     * @param gameId The current gameId.
     * @param start The offset/start of a move to be retrieved.
     * @param until The max limit of the moves that can be retrieved.
     * @return
     */
    @Nonnull
    public GetMovesResponse getMoves(@Nonnull String gameId, int start, int until) throws WebApplicationException {
        List<String> moveIds = new ArrayList<>();
        try {
            moveIds = gameDao.getMoves(gameId, Integer.valueOf(start), Integer.valueOf(until));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Game doesn't exist for the gameId {}", gameId);
            throwException(GAME_NOT_FOUND);
        }

        List<Move> moves = moveDao.getMoves(moveIds);

        List<GetMoveResponse> responses = moves.stream().map(move -> new GetMoveResponse(move))
                .collect(Collectors.toList()
             );


        return new GetMovesResponse.Builder().moves(responses).build();
    }

    /**
     * Retrieves a move for the provided input.
     *
     * @param gameId The current gameId.
     * @param moveId The moveId.
     * @return
     */
    @Nonnull
    GetMoveResponse getMove(@Nonnull String gameId, @Nonnull String moveId) {
        Game game = gameDao.getGame(gameId).orElse(null);
        Move move = moveDao.getMove(moveId).orElse(null);

        MoveByMoveIdValidator moveByMoveIdValidator = new MoveByMoveIdValidator();
        moveByMoveIdValidator.validate(game, move);

        return new GetMoveResponse(move);
    }

    /**
     * Retrieves all players of a game.
     *
     * @param gameId The current gameId
     * @return
     */
    @Nonnull
    public List<Player> getPlayers(@Nonnull String gameId) {
        Optional<Game> optionalGame = gameDao.getGame(gameId);
        if(optionalGame.isEmpty()) {
            throwException(GAME_NOT_FOUND);
        }

        return playerDao.getPlayers(optionalGame.get().getPlayerIds());
    }

    private void throwException(ExceptionStatusCodeAndMessage exceptionStatusCodeAndMessage) {
        throw new WebApplicationException(exceptionStatusCodeAndMessage.getMessage(), exceptionStatusCodeAndMessage.getStatusCode());
    }

    /**
     * Builds a 2-d array representing the grid for the game operations.
     *
     * @param gridValues Tracked by playerId dropping a token. The initial values of grid will be {@code 0}.
     * @param len defines the length of the grid. For a grid rows and columns will be same as length.
     * @return
     */
    private String[][] buildGrid(String[] gridValues, int len) {
        String[][] grid = new String[len][len];
        int count = 0;
        for(int i= 0; i<len; i++)
            for(int j =0; j< len; j++)
                grid[i][j] = gridValues[count++];

            return grid;
    }

    /**
     * Builds a list of grid values for a 2-d array.
     *
     * @param grid
     * @return
     */
    private List<String> getGridValues(String[][] grid) {
        int len = grid.length;
        String[] gridValues = new String[len * len];
        int count =0;
        for(int i = 0; i<len; i++)
            for(int j=0; j<len; j++)
                gridValues[count++] = grid[i][j];
        return Arrays.asList(gridValues);
    }
}
