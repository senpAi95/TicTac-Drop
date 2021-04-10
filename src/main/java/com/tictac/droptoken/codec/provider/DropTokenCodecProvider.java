package com.tictac.droptoken.codec.provider;

import com.tictac.droptoken.codec.GameCodec;
import com.tictac.droptoken.model.Game;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * A {@link CodecProvider} for providing the Custom Codecs for our application.
 */
public class DropTokenCodecProvider implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if(clazz == Game.class)
            return (Codec<T>) new GameCodec(registry);

        return null;
    }
}
