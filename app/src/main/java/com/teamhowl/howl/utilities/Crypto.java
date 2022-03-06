package com.teamhowl.howl.utilities;

public class Crypto {

    static {
        System.loadLibrary("howl");
    }

    public static native String stringFromJNI();

    public static String generateChatId(String localAddress, String foriegnAddress){

        return "123123123123";
    }

    public static String generateKeyPair(String chatId){

        String publicKey = "publicKey";

        return publicKey;
    }

    public static void storeForeignKey(String publicKey){


    }
}
