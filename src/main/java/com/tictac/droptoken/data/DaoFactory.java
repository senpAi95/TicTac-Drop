package com.tictac.droptoken.data;

import com.tictac.droptoken.data.impl.GameDaoImpl;
import com.tictac.droptoken.data.impl.GameStatusDaoImpl;
import com.tictac.droptoken.data.impl.MoveDaoImpl;
import com.tictac.droptoken.data.impl.PlayerDaoImpl;
import com.mongodb.client.MongoDatabase;

import javax.annotation.Nonnull;

/**
 * Provides lazy loaded {@code Singleton} instances for the dao.
 */
public class DaoFactory {

    private static GameDaoImpl gameDaoImpl = null;
    private static MoveDaoImpl moveDaoImpl= null;
    private static PlayerDaoImpl playerDaoImpl= null;
    private static GameStatusDaoImpl gameStatusDaoImpl = null;

    @Nonnull
    public static GameDao createGameDao(MongoDatabase database) {
        if(gameDaoImpl != null)
            return gameDaoImpl;
        return new GameDaoImpl(database);
    }
    @Nonnull
    public static MoveDao createMoveDao(MongoDatabase database) {
        if(moveDaoImpl != null)
            return moveDaoImpl;
        return new MoveDaoImpl(database);
    }
    @Nonnull
    public static PlayerDao createPlayerDao(MongoDatabase database) {
        if(playerDaoImpl != null)
            return playerDaoImpl;
        return new PlayerDaoImpl(database);
    }
    @Nonnull
    public static GameStatusDao createGameStatusDao(MongoDatabase database) {
        if (gameStatusDaoImpl != null)
            return gameStatusDaoImpl;
        return new GameStatusDaoImpl(database);
    }
}
