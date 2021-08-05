package com.tictac.drop.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Optional;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GameStatusResponse {
    private List<String> players;
    private String winner;
    private String state;

    public GameStatusResponse() {}

    private GameStatusResponse(Builder builder) {
        this.players = Preconditions.checkNotNull(builder.players);
        this.winner = builder.winner;
        this.state = Preconditions.checkNotNull(builder.state);
    }

    public List<String> getPlayers() {
        return players;
    }

    public Optional<String> getWinner() {
        return Optional.ofNullable(winner);
    }

    public String getState() {
        return state;
    }

    public static class Builder {
        private List<String> players;
        private String winner;
        private String state;

        public Builder players(List<String> players) {
            this.players = players;
            return this;
        }


        public Builder winner(String winner) {
            this.winner = winner;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder fromPrototype(GameStatusResponse prototype) {
            players = prototype.players;
            winner = prototype.winner;
            state = prototype.state;
            return this;
        }

        public GameStatusResponse build() {
            return new GameStatusResponse(this);
        }
    }
}
