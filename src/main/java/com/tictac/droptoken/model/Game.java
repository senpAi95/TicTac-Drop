package com.tictac.droptoken.model;


import org.bson.codecs.pojo.annotations.BsonId;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {
    @BsonId
    private java.lang.String id;

    private List<String> playerIds;
    private int length;
    private List<String> moveIds;

    private String[] gridValues;

    private String nextPlayerTurnId;

    private List<String> abandonedPlayers;

    public Game(){}

    public Game(String gameId, List<String> playerIds, int length) {
        this.id = gameId;
        this.length = length;
        this.playerIds = new ArrayList<>(playerIds);
        initialize();

    }
    private void initialize() {
        this.moveIds = new ArrayList<>();
        this.nextPlayerTurnId = playerIds.get(0);
        int limit = this.length * this.length;
        this.gridValues = new String[limit];
        while(limit>0) {
            gridValues[--limit] = "0";
        }
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public void setPlayerIds(List<String> playerIds) {
        this.playerIds = playerIds;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setMoveIds(List<String> moveIds) {
        this.moveIds = moveIds;
    }

    public void setGridValues(String[] gridValues) {
        this.gridValues = gridValues;
    }

    public java.lang.String getId() {
        return id;
    }

    public List<java.lang.String> getPlayerIds() {
        return playerIds;
    }

    public int getLength() {
        return length;
    }

    public List<String> getMoveIds() {
        return moveIds;
    }

    public String[] getGridValues() {
        return gridValues;
    }

    @Override
    public java.lang.String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", players=" + playerIds +
                ", columns=" + length +
                ", moves=" + moveIds +
                ", grid=" + gridValues +
                '}';
    }


    public String getNextPlayerTurnId() {
        return nextPlayerTurnId;
    }

    public void setNextPlayerTurnId(String playerTurnId) {
        this.nextPlayerTurnId = playerTurnId;
    }

    // TODO add abandoned players to the game.
    public List<String> getAbandonedPlayers() {
        return abandonedPlayers;
    }

    public void setAbandonedPlayers(List<String> abandonedPlayers) {
        this.abandonedPlayers = abandonedPlayers;
    }


    /**
     * Retrieves a player whose turn is next.
     *
     * @param inGamePlayerIds Current players in a sequential order.
     * @param playerId Current Player who finished posting a move.
     * @return {@link Optional<String>}
     */
    @Nonnull
    public Optional<String> nextPlayerInGame(@Nonnull List<String> inGamePlayerIds, @Nonnull String playerId) {
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
}
