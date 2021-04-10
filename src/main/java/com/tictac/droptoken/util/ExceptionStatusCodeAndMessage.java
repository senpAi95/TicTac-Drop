package com.tictac.droptoken.util;

/**
 * Provides exception messages with predefined HTTP status codes.
 *
 */
public enum ExceptionStatusCodeAndMessage {

    // 400 - Invalid Requests
    INVALID_GAME_REQUEST("Game request malformed.", 400),
    PLAYER_NAME_ALREADY_EXISTS("Player name should be unique.", 400),
    GAME_ALREADY_EXISTS("Game already exists.", 400),
    ILLEGAL_MOVE("Malformed input, Illegal move", 400),

    // 404 - Resource not found
    MOVE_NOT_FOUND("Move not found.", 404),
    GAME_NOT_FOUND("Game not found.", 404),
    PLAYER_NOT_FOUND("Player doesn't exist", 404),
    PLAYER_NOT_IN_GAME("Player is not in the game.", 404),

    // 409 - Conflict with target resource
    INVALID_PLAYER_GAME_TURN("Player tried to post when it's not their turn.", 409),

    // 410 - Resource state is permanent
    GAME_COMPLETED("Game is already in DONE state.", 410),

    // 500 - Internal Server error
    ERROR_RETRIEVING_GAME("Unable to retrieve game status. Please try later.", 500),
    PROCESSING_EXCEPTION("Unable to process the request at this time. Please try later.", 500);


    private final String message;
    private final int statusCode;

    ExceptionStatusCodeAndMessage(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;}
}
