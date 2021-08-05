package com.tictac.drop.model;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.List;
import java.util.Objects;

public class GameStatus {

    @BsonId
    private String id;

    private String status;

    private String winner;

    private List<String> playerIds;

    public List<String> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<String> playerIds) {
        this.playerIds = playerIds;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStatus gameStatus = (GameStatus) o;
        return id.equals(gameStatus.id) &&
                status.equals(gameStatus.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status);
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}
