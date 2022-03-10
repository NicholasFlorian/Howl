package com.teamhowl.howl.utilities;

public class Crypto {

    static {
        System.loadLibrary("howl");
    }

    public static native String stringFromJNI();

    public static String generateKeyPair(String chatId){

        String publicKey = "publicKey";

        return publicKey;
    }

    public static void storeForeignKey(String publicKey){


    }
}
