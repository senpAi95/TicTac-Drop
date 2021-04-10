package com.tictac.droptoken.data;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public abstract class CollectionDao<T> {

    final protected MongoCollection<T> mongoCollection;

    protected CollectionDao(MongoDatabase mongoDatabase, String collectionName, Class<T> clazz) {
        this.mongoCollection = mongoDatabase.getCollection(collectionName, clazz);
    }

}
