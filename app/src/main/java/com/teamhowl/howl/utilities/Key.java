package com.teamhowl.howl.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Key {
    private static final String TAG = "HOWL :: KeyService:";

    private final static String NAME = "com.teamhowl.howl.utilities.keystore";
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

    public static void store(
        Context context,
        String chatId,
        String keyType,
        String keyOrigin,
        String key){

        SharedPreferences preferences = context.getSharedPreferences(
            NAME,
            Context.MODE_PRIVATE);

        String keyId = createKeyId(chatId, keyType, keyOrigin);

        preferences.edit().putString(keyId, key);

        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        Log.d(TAG, "STORING :: " + keyId + " :: " + key);
        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
    }

    public static String retrieve(
        Context context,
        String chatId,
        String keyType,
        String keyOrigin){

        SharedPreferences preferences = context.getSharedPreferences(
                NAME,
                Context.MODE_PRIVATE);

        String keyId = createKeyId(chatId, keyType, keyOrigin);

        String key = preferences.getString(keyId, "");

        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        Log.d(TAG, "RETRIEVING :: " + keyId + " :: " + key);
        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");

        return key;
    }

}
