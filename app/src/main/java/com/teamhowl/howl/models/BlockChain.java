package com.teamhowl.howl.models;

import android.content.Context;
import android.util.Log;

import com.teamhowl.howl.controllers.PendingBlockDao;
import com.teamhowl.howl.controllers.StashedBlockDao;
import com.teamhowl.howl.repositories.BlockRoomDatabase;
import com.teamhowl.howl.utilities.Key;

import java.sql.Date;
import java.util.ArrayList;

public class BlockChain {
    private final String TAG = "HOWL :: BlockChain:";
    private final String TAG_JNI = "HOWL :: BlockChain :: JNI:";

    static {
        System.loadLibrary("howl");
    }

    private long blockChainPointer;
    private String chatId;
    private Context context;
    private BlockRoomDatabase blockRoomDatabase;
    private ArrayList<Message> messages;


    /** JNI call */
    private native long buildBlockChain(String chatId);

    private native void buildGenesisBlock(Long pointer);

    private native void buildSentBlock(long pointer, String plainText);

    private native String addReceivedBlock(long pointer, String encryptedBlock, String publicKey);

    private native String addPrevSentBlock(long pointer, String encryptedBlock, String publicKey);

    private native String getEncryptedBlock(long pointer, String privateKey);

    private native void cleanup(long pointer);

    public static native void testMainCode();

    /** Java Code **/
    public BlockChain(Context context, String chatId) {
        Log.d(TAG, "Construct BlockChain");

        this.chatId = chatId;
        this.context = context;
        this.blockRoomDatabase = BlockRoomDatabase.getDatabase(context);
        this.messages = new ArrayList<>();
        this.blockChainPointer = buildBlockChain(chatId);

        Log.d(TAG_JNI, "Pointer :: " + this.blockChainPointer);
    }

    public void refresh(){
        Log.d(TAG, "Refresh");

        PendingBlockDao pendingBlockDao = blockRoomDatabase.pendingBlockDao();
        StashedBlockDao stashedBlockDao = blockRoomDatabase.stashedBlockDao();

        cleanup(blockChainPointer);
        this.blockChainPointer = buildBlockChain(chatId);
        this.messages.clear();

        for(PendingBlock block : pendingBlockDao.findBlocksByChatId(chatId))
            addPrevSentMessage(block);

        for(StashedBlock block : stashedBlockDao.findBlocksByChatId(chatId))
            addReceivedMessage(block);
    }

    public void destroy(){

        cleanup(blockChainPointer);
    }

    public PendingBlock buildGenesisMessage(){

        Log.d(TAG, "Build Genesis Message");
        Log.d(TAG_JNI, "buildGenesisBlock()");
        buildGenesisBlock(blockChainPointer);

        Log.d(TAG_JNI, "getEncryptedBlock()");
        String encryptedBlock = getEncryptedBlock(
                blockChainPointer,
                Key.retrieve(context, chatId, Key.PRIVATE_KEY, Key.LOCAL_KEY));

        return new PendingBlock(chatId, encryptedBlock);
    }

    public PendingBlock buildMessage(String message) {

        Log.d(TAG, "Build Genesis Message: " + message);
        Log.d(TAG_JNI, "buildSentBlock()");
        buildSentBlock(blockChainPointer, message);

        Log.d(TAG_JNI, "getEncryptedBlock()");
        String encryptedBlock = getEncryptedBlock(
                blockChainPointer,
                Key.retrieve(context, chatId, Key.LOCAL_KEY, Key.PRIVATE_KEY));

        return new PendingBlock(chatId, encryptedBlock);
    }

    public void addPrevSentMessage(PendingBlock block) {

        Log.d(TAG, "Add Previously Sent Message");
        Log.d(TAG_JNI, "addPrevSentBlock()");
        String plainTextBlock = addPrevSentBlock(
            blockChainPointer,
            block.getEncryptedBlock(),
            Key.retrieve(context, chatId, Key.PUBLIC_KEY, Key.LOCAL_KEY));

        Message newMessage = new Message(plainTextBlock, new Date(0));
        messages.add(newMessage);
    }

    public void addReceivedMessage(StashedBlock block) {

        Log.d(TAG, "Add Received Message");
        Log.d(TAG_JNI, "addReceivedBlock()");
        String plainTextBlock = addReceivedBlock(
            blockChainPointer,
            block.getEncryptedBlock(),
            Key.retrieve(context, chatId, Key.FOREIGN_KEY, Key.PUBLIC_KEY));

        Message newMessage = new Message(plainTextBlock, new Date(0), new Date(0));
        messages.add(newMessage);
    }

    public void checkNewMessage(PooledBlock block) throws SecurityException {

        refresh();
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
    
}
