package com.tictac.drop;

import com.tictac.drop.model.CreateGameRequest;
import com.tictac.drop.model.CreateGameResponse;
import com.tictac.drop.model.GameStatusResponse;
import com.tictac.drop.model.GetGamesResponse;
import com.tictac.drop.model.GetMoveResponse;
import com.tictac.drop.model.GetMovesResponse;
import com.tictac.drop.model.Player;
import com.tictac.drop.model.PostMoveRequest;
import com.tictac.drop.model.PostMoveResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
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
 * Resource file for providing the API functionality.
 */
@Path("/drop_token")
@Produces(MediaType.APPLICATION_JSON)
public class TicTacDropResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(TicTacDropResource.class);

    @Inject
    TicTacDropService ticTacDropService;

    @GET
    public Response getGames() {
        List<String> gameIds = ticTacDropService.getInProgressGames();
        return Response.ok(new GetGamesResponse.Builder().games(gameIds).build()).build();
    }

    @POST
    public Response createNewGame(CreateGameRequest request) {
        LOGGER.info("request={}", request);
        final String gameId = ticTacDropService.createNewGame(request);
        return Response.ok(new CreateGameResponse.Builder().gameId(gameId).build()).build();
    }

    @Path("/{id}")
    @GET
    public Response getGameStatus(@PathParam("id") String gameId) {
        LOGGER.info("gameId = {}", gameId);
        final GameStatusResponse gameStatusResponse = ticTacDropService.getGameStatus(gameId);
        return Response.ok(gameStatusResponse).build();
    }

    @Path("/{id}/players")
    @GET
    public Response getPlayers(@PathParam("id")String gameId) {
        LOGGER.info("gameId = {}", gameId);
        final List<Player> players = ticTacDropService.getPlayers(gameId);

        return Response.ok(players).build();
    }


    @Path("/{id}/players/{playerId}")
    @POST
    public Response postMove(@PathParam("id")String gameId, @PathParam("playerId") String playerId, PostMoveRequest request) {
        LOGGER.info("gameId={}, playerId={}, move={}", gameId, playerId, request);
        PostMoveResponse postMoveResponse = ticTacDropService.postMove(gameId, playerId, request.getColumn());
        return Response.ok(postMoveResponse).build();
    }

    @Path("/{id}/players/{playerId}")
    @DELETE
    public Response playerQuit(@PathParam("id")String gameId, @PathParam("playerId") String playerId) {
        LOGGER.info("gameId={}, playerId={}", gameId, playerId);
        ticTacDropService.playerQuit(gameId, playerId);
        return Response.status(202).build();
    }

    @Path("/{id}/moves")
    @GET
    public Response getMoves(@PathParam("id") String gameId,@DefaultValue("0") @QueryParam("start") Integer start,@DefaultValue("5") @QueryParam("until") Integer until) {
        LOGGER.info("gameId={}, start={}, until={}", gameId, start, until);
        GetMovesResponse getMovesResponse = ticTacDropService.getMoves(gameId, start, until);
        return Response.ok(getMovesResponse).build();
    }

    @Path("/{id}/moves/{moveId}")
    @GET
    public Response getMove(@PathParam("id") String gameId, @PathParam("moveId") String moveId) {
        LOGGER.info("gameId={}, moveId={}", gameId, moveId);
        GetMoveResponse getMoveResponse = ticTacDropService.getMove(gameId, moveId);
        return Response.ok(getMoveResponse).build();
    }

}
