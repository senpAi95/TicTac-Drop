package com.tictac.drop.codec;

import com.tictac.drop.model.Game;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A custom codec for persisting and retrieving a {@link Game} object in MongoDb.
 */
public class GameCodec implements Codec<Game> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameCodec.class);
    private static final String ID = "_id";
    private static final String PLAYERS = "players";
    private static final String LENGTH= "length";
    private static final String MOVE_IDS = "moveIds";
    private static final String GRID_VALUES = "gridValues";
    private static final String NEXT_PLAYER_TURN_ID = "nextPlayerTurnId";

    CodecRegistry codecRegistry;

    public GameCodec(final CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public Class<Game> getEncoderClass() {
        return Game.class;
    }

    @Override
    public Game decode(BsonReader bsonReader, DecoderContext decoderContext) {
        Game game = new Game();
        bsonReader.readStartDocument();
        while(bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            final java.lang.String name = bsonReader.readName();
            switch(name) {
                case ID:
                    game.setId(bsonReader.readString());
                    break;
                case PLAYERS:
                    bsonReader.readStartArray();
                    List<String> playerList = new ArrayList<>();
                    while(bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        playerList.add(bsonReader.readString());
                    }
                    game.setPlayerIds(playerList);
                    bsonReader.readEndArray();

                    break;
                case LENGTH:
                    game.setLength(bsonReader.readInt32());

                    break;
                case MOVE_IDS:
                    List<String> strings = new ArrayList<>();
                    bsonReader.readStartArray();
                    while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        strings.add(bsonReader.readString());
                    }
                    bsonReader.readEndArray();
                    game.setMoveIds(strings);
                    break;
                case GRID_VALUES:
                    String[] gridValues = new String[game.getLength()*game.getLength()];
                    bsonReader.readStartArray();
                    int index =0;
                    while(bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        gridValues[index++] = bsonReader.readString();
                    }
                    bsonReader.readEndArray();
                    game.setGridValues(gridValues);
                    break;
                case NEXT_PLAYER_TURN_ID:
                    game.setNextPlayerTurnId(bsonReader.readString());
                    break;
                default: new WebApplicationException("Unexpected Exception occured", 500);
            }
        }
        bsonReader.readEndDocument();
        return game;
    }

    @Override
    public void encode(BsonWriter bsonWriter, Game game, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();
            bsonWriter.writeString(ID, game.getId());
                bsonWriter.writeStartArray(PLAYERS);
                    Iterator<java.lang.String> playerId = game.getPlayerIds().iterator();
                    while(playerId.hasNext()) {
                        bsonWriter.writeString(playerId.next());
                    }
                bsonWriter.writeEndArray();
            bsonWriter.writeInt32(LENGTH, game.getLength());

        bsonWriter.writeStartArray(MOVE_IDS);
            Iterator<String> moveIterator = game.getMoveIds().iterator();

            while(moveIterator.hasNext()) {
                bsonWriter.writeString(moveIterator.next());
            }

        bsonWriter.writeEndArray();
            bsonWriter.writeStartArray(GRID_VALUES);
        int gridLen = game.getLength();
        String[] gridValues = game.getGridValues();
        for(int i = 0; i< gridLen * gridLen; i++)
                bsonWriter.writeString(gridValues[i]);
        bsonWriter.writeEndArray();
        bsonWriter.writeString(NEXT_PLAYER_TURN_ID, game.getNextPlayerTurnId());
        bsonWriter.writeEndDocument();
    }

}
