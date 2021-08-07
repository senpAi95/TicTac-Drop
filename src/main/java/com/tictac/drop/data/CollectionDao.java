package com.tictac.drop.data;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Abstract class for providing CollectionDao interacting with MongoDB.
 *
 * @param <T>
 */
public abstract class CollectionDao<T> {

    final protected MongoCollection<T> mongoCollection;

    protected CollectionDao(MongoDatabase mongoDatabase, String collectionName, Class<T> clazz) {
        this.mongoCollection = mongoDatabase.getCollection(collectionName, clazz);
    }

}
