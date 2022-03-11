package com.teamhowl.howl.utilities;

public class Crypto {

    /** Load cryptography code **/
    static {
        System.loadLibrary("howl");
    }

    public static native void loadSSL();

    public static native String[] generateKeyPair();

    public static native String generateChatId(String localUserId, String foreignUserId);

    public static native String generateUserId(String localAddress);

}
