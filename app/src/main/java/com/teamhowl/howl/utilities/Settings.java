package com.teamhowl.howl.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

    private final static String NAME = "com.teamhowl.howl.utilities.Settings";
    private final static String DEVICE_NAME = "DEVICE_NAME";
    private final static String USER_NAME = "USER_NAME";
    private final static String NOTIFICATION = "NOTIFICATION";

    public static void attemptFirstTimeSettings(Context context, String deviceName){

        SharedPreferences preferences = context.getSharedPreferences(
                NAME,
                Context.MODE_PRIVATE);

        setDeviceName(context, deviceName);

        if(!preferences.contains(USER_NAME))
            setUserName(context, deviceName);

        if(!preferences.contains(NOTIFICATION))
            setNotification(context, true);
    }

    public static String getDeviceName(Context context){

        SharedPreferences preferences = context.getSharedPreferences(
                NAME,
                Context.MODE_PRIVATE);

        String userName = preferences.getString(DEVICE_NAME, "");

        return userName;
    }

    public static void setDeviceName(Context context, String value){

        SharedPreferences preferences = context.getSharedPreferences(
                NAME,
                Context.MODE_PRIVATE);

        preferences.edit().putString(DEVICE_NAME, value).apply();
    }

    public static String getUserName(Context context){

        SharedPreferences preferences = context.getSharedPreferences(
                NAME,
                Context.MODE_PRIVATE);

        String userName = preferences.getString(USER_NAME, "");

        return userName;
    }

    public static void setUserName(Context context, String value){

        SharedPreferences preferences = context.getSharedPreferences(
                NAME,
                Context.MODE_PRIVATE);

        preferences.edit().putString(USER_NAME, value).apply();
    }

    public static boolean getNotification(Context context){

        SharedPreferences preferences = context.getSharedPreferences(
                NAME,
                Context.MODE_PRIVATE);

        boolean notification = preferences.getBoolean(NOTIFICATION, true);

        return notification;
    }

    public static void setNotification(Context context, boolean value){

        SharedPreferences preferences = context.getSharedPreferences(
                NAME,
                Context.MODE_PRIVATE);

        preferences.edit().putBoolean(NOTIFICATION, value).apply();
    }
}
