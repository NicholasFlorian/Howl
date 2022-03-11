package com.teamhowl.howl.utilities;

public class KeyStore {

    public final static String PUBLIC_KEY   = "PUBLIC";
    public final static String PRIVATE_KEY  = "PRIVATE";
    public final static String LOCAL_KEY    = "LOCAL";
    public final static String FOREIGN_KEY  = "FOREIGN";

    private static String createKeyId(
            String chatId,
            String keyType,
            String keyOrigin){

        return chatId + "_" + keyType + "_" + keyOrigin + "_KEY";
     }

    public static void storeKey(
        String chatId,
        String keyType,
        String keyOrigin,
        String key){

        String keyId = createKeyId(chatId, keyType, keyOrigin);

    }

    public static String getKey(
        String chatId,
        String keyType,
        String keyOrigin,
        String key){

        String keyId = createKeyId(chatId, keyType, keyOrigin);

        return "key";
    }

}
