package com.tictac.droptoken.util;

import java.util.Iterator;
import java.util.List;

import static java.util.UUID.nameUUIDFromBytes;

/**
 * A UID generator for a string and list of String for generating UIDs through out the game.
 */
public class UidGenerator {

    public static String generateUid(String string) {
        return nameUUIDFromBytes(string.getBytes()).toString();
    }

    public static String generateUid(List<String> strings) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<String> stringIterator = strings.iterator();
        while(stringIterator.hasNext()) {
            stringBuffer.append(stringIterator.next());
        }
        return generateUid(stringBuffer.toString());
    }
}
