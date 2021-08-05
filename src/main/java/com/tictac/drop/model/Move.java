package com.tictac.drop.model;

import com.tictac.drop.util.UidGenerator;
import org.bson.codecs.pojo.annotations.BsonId;

import java.io.Serializable;
import java.time.Instant;

public class Move implements Serializable {
    @BsonId
    String id;
    String type;
    String player;
    int column;

    public Move(){}

    public Move(String type, String player, int column) {
        id = UidGenerator.generateUid(Instant.now().toString());
        this.type = type;
        this.player = player;
        this.column = column;
    }

    public Move(String type, String player) {
        id = UidGenerator.generateUid(Instant.now().toString());
        this.type = type;
        this.player = player;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public void setType(java.lang.String type) {
        this.type = type;
    }

    public void setPlayer(java.lang.String player) {
        this.player = player;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public java.lang.String getId() {
        return id;
    }

    public java.lang.String getType() {
        return type;
    }

    public java.lang.String getPlayer() {
        return player;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Move{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", player='" + player + '\'' +
                ", column=" + column +
                '}';
    }
}
