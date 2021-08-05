package com.tictac.drop.model;

public class GetMoveResponse {
    Move move;

    public GetMoveResponse(Move move) {
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    @Override
    public String toString() {
        return "GetMoveResponse{" +
                "move=" + move +
                '}';
    }
}
