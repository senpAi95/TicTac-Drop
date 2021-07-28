package com.tictac.droptoken;

import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.tictac.droptoken.data.*;
import com.tictac.droptoken.model.*;
import com.tictac.droptoken.util.ExceptionStatusCodeAndMessage;
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

import static com.tictac.droptoken.util.UidGenerator.generateUid;

/**
 * A service which can handle API requests and delegate them to DAOs
 */
public class DropTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DropTokenService.class);
    private static final String MOVE = "MOVE";
    private static final String QUIT = "QUIT";
    private static final String GAME_STATUS_DONE = "DONE";
    private static final int MIN_GRID_PLAYERS = 2;
    private static final int MIN_GRID_LENGTH = 4;

    private MongoDatabase database;
    private GameDao gameDao;
    private MoveDao moveDao;
    private PlayerDao playerDao;
    private GameStatusDao gameStatusDao;

    @Inject
    GridOperations gridOperations;

    public DropTokenService(MongoDatabase database) {
        this.database = database;
        initialize();
    }

    private void initialize() {
        gameDao = DaoFactory.createGameDao(database);
        moveDao = DaoFactory.createMoveDao(database);
        playerDao = DaoFactory.createPlayerDao(database);
        gameStatusDao = DaoFactory.createGameStatusDao(database);
    }

    /**
     * Retrieves all IN_PROGRESS games.
     *
     * @return a list of gameIds
     */
    @Nonnull
    public List<String> getInProgressGames() {
        System.out.println(gridOperations);
        LOGGER.info("Retrieving in progress games");
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
    public String createNewGame(CreateGameRequest request) throws WebApplicationException{
        LOGGER.debug("Creating new game");
        List<String> requestPlayers = request.getPlayers();
        boolean NoMinPlayers = requestPlayers.size() < MIN_GRID_PLAYERS;
        boolean notEqualColumnsAndRows = request.getColumns() != request.getRows();
        boolean NoMinGridLength = request.getColumns() < MIN_GRID_LENGTH;

        if(NoMinPlayers) {
            LOGGER.error("Minimum players should be at least 2. Given players size: {}", requestPlayers.size());
            throwException(ExceptionStatusCodeAndMessage.INVALID_GAME_REQUEST);
        }
        if(notEqualColumnsAndRows) {
            LOGGER.error("Columns and rows should match for constructing a grid, given columns: {} given rows: {}",
                    request.getColumns(), request.getRows());
            throwException(ExceptionStatusCodeAndMessage.INVALID_GAME_REQUEST);
        }
        if(NoMinGridLength) {
            LOGGER.error("Minimum columns for the grid should be at least 4. Given grid length: {}", request.getColumns());
            throwException(ExceptionStatusCodeAndMessage.INVALID_GAME_REQUEST);
        }

        List<String> distinctSortedPlayers = requestPlayers.stream().distinct().sorted().collect(Collectors.toList());
        if(requestPlayers.size() != distinctSortedPlayers.size()) {
            LOGGER.error("Player names should be unique");
            throwException(ExceptionStatusCodeAndMessage.PLAYER_NAME_ALREADY_EXISTS);
        }

        // can same players play multiple games in parallel? if so, append timestamp to sortedPlayers.
        final String gameId = generateUid(distinctSortedPlayers);

        try {
            List<String> playerIds = playerDao.addPlayers(distinctSortedPlayers, gameId);
            gameDao.createGame(gameId, request, playerIds);
            gameStatusDao.createGameStatus(gameId, playerIds);
        }  catch (MongoException e) {
            LOGGER.error("Unable to process the request because of {}", e);
            throwException(ExceptionStatusCodeAndMessage.PROCESSING_EXCEPTION);
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
    public GameStatusResponse getGameStatus(String gameId) {
        LOGGER.debug("Retrieving game status {}", gameId);
        GameStatusResponse.Builder builder = new GameStatusResponse.Builder();
        Optional<GameStatus> optionalGameStatus = gameStatusDao.getGameStatus(gameId);

        if(optionalGameStatus.isEmpty()) {
            throwException(ExceptionStatusCodeAndMessage.GAME_NOT_FOUND);
        }

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
    public PostMoveResponse postMove(String gameId, String playerId, int column) {
        Optional<Game> optionalGame = gameDao.getGame(gameId);
        Optional<Player> optionalPlayer = playerDao.getPlayer(playerId);
        Optional<GameStatus> optionalGameStatus = gameStatusDao.getGameStatus(gameId);

        if(optionalGame.isEmpty() || optionalGameStatus.isEmpty()) {
            LOGGER.error("Unable to find game for id {}", gameId);
            throwException(ExceptionStatusCodeAndMessage.GAME_NOT_FOUND);
        }
        Game game = optionalGame.get();
        GameStatus gameStatus = optionalGameStatus.get();

        if(optionalPlayer.isEmpty()) {
            LOGGER.error("PlayerId {} doesn't exist",playerId);
            throwException(ExceptionStatusCodeAndMessage.PLAYER_NOT_FOUND);
        }
        Player player = optionalPlayer.get();

        if(!game.getPlayerIds().contains(playerId)) {
            LOGGER.error("PlayerId {} doesn't belong to gameId {}",playerId, gameId);
            throwException(ExceptionStatusCodeAndMessage.PLAYER_NOT_IN_GAME);
        }

        if(gameStatus.getStatus().equals(GAME_STATUS_DONE)) {
            throwException(ExceptionStatusCodeAndMessage.GAME_COMPLETED);
        }

        if(!optionalGame.get().getNextPlayerTurnId().equals(playerId)) {
            throwException(ExceptionStatusCodeAndMessage.INVALID_PLAYER_GAME_TURN);
        }

        String[][] grid = buildGrid(optionalGame.get().getGridValues(), optionalGame.get().getLength());
        int rowIndex = GridOperations.getPossibleRow(grid, column);

        if(rowIndex == -1) {
            throwException(ExceptionStatusCodeAndMessage.ILLEGAL_MOVE);
        }
        grid[rowIndex][column-1] = playerId;

        Move move = new Move(MOVE, player.getName(), column);

        // updateGrid and add Move.
         optionalGame.get().getMoveIds().add(move.getId());

         if(GridOperations.winningMove(grid, playerId, rowIndex, column))
             gameStatusDao.updateGameStatusToCompleted(gameId, player.getName());

            //update gameDao, movesDao
        moveDao.createMove(move);
        Optional<String> nextPlayer =  nextPlayerInGame(game.getPlayerIds(), playerId);
        gameDao.addMove(gameId, move.getId(), getGridValues(grid), nextPlayer);
        String moveLink = createMoveLink(gameId, move.getId());

        return new PostMoveResponse.Builder().moveLink(moveLink).build();
    }

    /**
     * Retrieves a player whose turn is next.
     *
     * @param inGamePlayerIds Current players in a sequential order.
     * @param playerId Current Player who finished posting a move.
     * @return {@link Optional<String>}
     */
    @Nonnull
    Optional<String> nextPlayerInGame(@Nonnull List<String> inGamePlayerIds,@Nonnull String playerId) {
        int indexOfCurrentPlayer = inGamePlayerIds.indexOf(playerId);
        Optional<String> nextPlayer = Optional.empty();
        if(inGamePlayerIds.size()!=1) {
            if(indexOfCurrentPlayer == inGamePlayerIds.size()-1) {
                return Optional.of(inGamePlayerIds.get(0));
            } else {
                return  Optional.of(inGamePlayerIds.get(indexOfCurrentPlayer + 1));
            }
        }
        return nextPlayer;
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
    public void playerQuit(@Nonnull String gameId, @Nonnull String playerId) {
        Optional<Game> optionalGame = gameDao.getGame(gameId);
        Optional<Player> optionalPlayer = playerDao.getPlayer(playerId);
        Optional<GameStatus> optionalGameStatus = gameStatusDao.getGameStatus(gameId);

        if(optionalGame.isEmpty()) {
            LOGGER.error("Unable to find game for id {}", gameId);
            throwException(ExceptionStatusCodeAndMessage.GAME_NOT_FOUND);
        }
        Game game = optionalGame.get();

        if(optionalGameStatus.isEmpty()) {
            LOGGER.error("Unable to find gameStatus for id {}", gameId);
            throwException(ExceptionStatusCodeAndMessage.GAME_NOT_FOUND);
        }
        GameStatus gameStatus = optionalGameStatus.get();

        if(optionalPlayer.isEmpty()) {
            LOGGER.error("PlayerId {} doesn't exist",playerId);
            throwException(ExceptionStatusCodeAndMessage.PLAYER_NOT_FOUND);
        }
        Player player = optionalPlayer.get();

        if(!game.getPlayerIds().contains(playerId)) {
            LOGGER.error("PlayerId {} doesn't belong to gameId {}",playerId, gameId);
            throwException(ExceptionStatusCodeAndMessage.PLAYER_NOT_IN_GAME);
        }

        if(gameStatus.getStatus().equals(GAME_STATUS_DONE)) {
            throwException(ExceptionStatusCodeAndMessage.GAME_COMPLETED);
        }

        Move move = new Move(QUIT, player.getName());

        moveDao.createMove(move);

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
            throwException(ExceptionStatusCodeAndMessage.PROCESSING_EXCEPTION);
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
    public GetMovesResponse getMoves(@Nonnull String gameId, int start, int until) {
        List<String> moveIds = new ArrayList<>();
        try {
            moveIds = gameDao.getMoves(gameId, Integer.valueOf(start), Integer.valueOf(until));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Game doesn't exist for the gameId {}", gameId);
            throwException(ExceptionStatusCodeAndMessage.GAME_NOT_FOUND);
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
        Optional<Game> optionalGame = gameDao.getGame(gameId);
        Optional<Move> optionalMove = moveDao.getMove(moveId);
        if(optionalGame.isEmpty()) {
            throwException(ExceptionStatusCodeAndMessage.GAME_NOT_FOUND);
        }
        if(optionalMove.isEmpty()) {
            throwException(ExceptionStatusCodeAndMessage.MOVE_NOT_FOUND);
        }
        return new GetMoveResponse(optionalMove.get());
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
            throwException(ExceptionStatusCodeAndMessage.GAME_NOT_FOUND);
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
