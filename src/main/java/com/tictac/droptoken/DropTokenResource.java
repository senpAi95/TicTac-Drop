package com.tictac.droptoken;

import com.tictac.droptoken.model.CreateGameRequest;
import com.tictac.droptoken.model.CreateGameResponse;
import com.tictac.droptoken.model.GameStatusResponse;
import com.tictac.droptoken.model.GetGamesResponse;
import com.tictac.droptoken.model.GetMoveResponse;
import com.tictac.droptoken.model.GetMovesResponse;
import com.tictac.droptoken.model.Player;
import com.tictac.droptoken.model.PostMoveRequest;
import com.tictac.droptoken.model.PostMoveResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 *
 */
@Path("/drop_token")
@Produces(MediaType.APPLICATION_JSON)
public class DropTokenResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DropTokenResource.class);

    @Inject
    DropTokenService dropTokenService;

    @GET
    public Response getGames() {
        List<String> gameIds = dropTokenService.getInProgressGames();
        return Response.ok(new GetGamesResponse.Builder().games(gameIds).build()).build();
    }

    @POST
    public Response createNewGame(CreateGameRequest request) {
        LOGGER.info("request={}", request);
        final String gameId = dropTokenService.createNewGame(request);
        return Response.ok(new CreateGameResponse.Builder().gameId(gameId).build()).build();
    }

    @Path("/{id}")
    @GET
    public Response getGameStatus(@PathParam("id") String gameId) {
        LOGGER.info("gameId = {}", gameId);
        final GameStatusResponse gameStatusResponse = dropTokenService.getGameStatus(gameId);
        return Response.ok(gameStatusResponse).build();
    }

    @Path("/{id}/players")
    @GET
    public Response getPlayers(@PathParam("id")String gameId) {
        LOGGER.info("gameId = {}", gameId);
        final List<Player> players = dropTokenService.getPlayers(gameId);

        return Response.ok(players).build();
    }


    @Path("/{id}/players/{playerId}")
    @POST
    public Response postMove(@PathParam("id")String gameId, @PathParam("playerId") String playerId, PostMoveRequest request) {
        LOGGER.info("gameId={}, playerId={}, move={}", gameId, playerId, request);
        PostMoveResponse postMoveResponse = dropTokenService.postMove(gameId, playerId, request.getColumn());
        return Response.ok(postMoveResponse).build();
    }

    @Path("/{id}/players/{playerId}")
    @DELETE
    public Response playerQuit(@PathParam("id")String gameId, @PathParam("playerId") String playerId) {
        LOGGER.info("gameId={}, playerId={}", gameId, playerId);
        dropTokenService.playerQuit(gameId, playerId);
        return Response.status(202).build();
    }

    @Path("/{id}/moves")
    @GET
    public Response getMoves(@PathParam("id") String gameId,@DefaultValue("0") @QueryParam("start") Integer start,@DefaultValue("5") @QueryParam("until") Integer until) {
        LOGGER.info("gameId={}, start={}, until={}", gameId, start, until);
        GetMovesResponse getMovesResponse = dropTokenService.getMoves(gameId, start, until);
        return Response.ok(getMovesResponse).build();
    }

    @Path("/{id}/moves/{moveId}")
    @GET
    public Response getMove(@PathParam("id") String gameId, @PathParam("moveId") String moveId) {
        LOGGER.info("gameId={}, moveId={}", gameId, moveId);
        GetMoveResponse getMoveResponse = dropTokenService.getMove(gameId, moveId);
        return Response.ok(getMoveResponse).build();
    }

}
