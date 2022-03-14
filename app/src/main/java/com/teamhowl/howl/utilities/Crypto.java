package com.teamhowl.howl.utilities;

import java.util.ArrayList;
import java.util.Collections;

public class Crypto {

    /** Load cryptography code **/
    static {
        System.loadLibrary("howl");
    }

    public static native void loadSSL();

    // public then private
    public static native String[] generateKeyPair();

    public static native String generateChatId(String localUserId, String foreignUserId);

    public static native String generateUserId(String localAddress);

    public static String generateSortChatId(String localUserId, String foreignUserId){

        ArrayList<String> ids;

        ids = new ArrayList<>();
        ids.add(localUserId);
        ids.add(foreignUserId);
        Collections.sort(ids);

        return generateChatId(ids.get(0), ids.get(1));
    }

}
