package com.tictac.droptoken;

/**
 * Contains the collection names associated to MongoDB.
 */
public enum CollectionNames {
    GAME("game"),
    MOVES("moves"),
    PLAYERS("players"),
    STATUS("status");

    final String name;

    CollectionNames(String name) {
        this.name = name;
    }

    public String getValue(){
        return this.name;
    }

}
