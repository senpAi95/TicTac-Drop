package com.tictac.drop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 */

public class TicTacDropConfiguration extends Configuration {
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

    @NotEmpty
    private String databaseName;

    @NotEmpty
    private String dbConnectionString;

    @NotEmpty
    private String minPlayers;

    @NotEmpty
    private String minGridLength;

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDbConnectionString() {
        return dbConnectionString;
    }

    @JsonProperty
    public void setDbConnectionString(String dbConnectionString) {
        this.dbConnectionString = dbConnectionString;
    }

    @JsonProperty
    public String getMinPlayers() {
        return minPlayers;
    }

    @JsonProperty
    public void setMinPlayers(String minPlayers) {
        this.minPlayers = minPlayers;
    }

    @JsonProperty
    public String getMinGridLength() {
        return minGridLength;
    }

    @JsonProperty
    public void setMinGridLength(String minGridLength) {
        this.minGridLength = minGridLength;
    }

    @JsonProperty
    public String getDatabaseName() {
        return databaseName;
    }

    @JsonProperty
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
