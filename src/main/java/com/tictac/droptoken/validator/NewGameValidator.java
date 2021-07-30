package com.tictac.droptoken.validator;

import com.tictac.droptoken.model.CreateGameRequest;
import com.tictac.droptoken.util.ExceptionStatusCodeAndMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.stream.Collectors;

import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.INVALID_GAME_REQUEST;
import static com.tictac.droptoken.util.ExceptionStatusCodeAndMessage.PLAYER_NAME_ALREADY_EXISTS;

public class NewGameValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewGameValidator.class);

    private final int minPlayers;
    private final int minGridLength;

    @Inject
    public NewGameValidator(@Named("minPlayers") int minPlayers, @Named("minGridLength") int minGridLength) {
        this.minPlayers = minPlayers;
        this.minGridLength = minGridLength;
    }

    void validateMinPlayers(CreateGameRequest request) throws WebApplicationException {
        int gameRequestPlayerSize = request.getPlayers().size();
        boolean NoMinPlayers = gameRequestPlayerSize < minPlayers;
        if(NoMinPlayers) {
            LOGGER.error("Minimum players should be at least 2. Given players size: {}", gameRequestPlayerSize);
            throwException(INVALID_GAME_REQUEST);
        }
    }

    void validateRowsAndColumns(CreateGameRequest request) throws WebApplicationException {
        if(request.getColumns() != request.getRows()) {
            LOGGER.error("Columns and rows should match for constructing a grid, given columns: {} given rows: {}",
                    request.getColumns(), request.getRows());
            throwException(INVALID_GAME_REQUEST);
        }
    }

    void validateMinGridLength(CreateGameRequest request) throws WebApplicationException {
        if(request.getColumns() < minGridLength) {
            LOGGER.error("Minimum columns for the grid should be at least 4. Given grid length: {}", request.getColumns());
            throwException(INVALID_GAME_REQUEST);
        }
    }

    void validateDistinctPlayers(CreateGameRequest request) throws WebApplicationException {
        List<String> requestPlayers = request.getPlayers();
        List<String> distinctSortedPlayers = requestPlayers.stream().distinct().sorted().collect(Collectors.toList());

        if(requestPlayers.size() != distinctSortedPlayers.size()) {
            LOGGER.error("Player names should be unique");
            throwException(PLAYER_NAME_ALREADY_EXISTS);
        }
    }

    public void validate(CreateGameRequest request) throws WebApplicationException {
        validateMinPlayers(request);
        validateRowsAndColumns(request);
        validateMinGridLength(request);
        validateDistinctPlayers(request);
    }

    void throwException(ExceptionStatusCodeAndMessage exceptionStatusCodeAndMessage) throws WebApplicationException{
        throw new WebApplicationException(exceptionStatusCodeAndMessage.getMessage(), exceptionStatusCodeAndMessage.getStatusCode());
    }
}
