package com.teamhowl.howl.models;

import android.content.Context;

import com.teamhowl.howl.controllers.PendingBlockDao;
import com.teamhowl.howl.controllers.StashedBlockDao;
import com.teamhowl.howl.repositories.BlockRoomDatabase;
import com.teamhowl.howl.utilities.Key;

import java.sql.Date;
import java.util.ArrayList;

public class BlockChain {

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

    /** Java Code **/
    public BlockChain(String chatId, Context context) {

        this.chatId = chatId;
        this.context = context;
        this.blockRoomDatabase = BlockRoomDatabase.getDatabase(context);
        this.messages = new ArrayList<>();

        refresh();
    }

    public void refresh(){

        PendingBlockDao pendingBlockDao = blockRoomDatabase.pendingBlockDao();
        StashedBlockDao stashedBlockDao = blockRoomDatabase.stashedBlockDao();

        cleanup(blockChainPointer);
        this.blockChainPointer = buildBlockChain(chatId);

        for(PendingBlock block : pendingBlockDao.findBlocksByChatId(chatId))
            addPrevSentMessage(block);

        for(StashedBlock block : stashedBlockDao.findBlocksByChatId(chatId))
            addReceivedMessage(block);

    }

    public void destroy(){

        cleanup(blockChainPointer);
    }

    public PendingBlock buildGenesisMessage(){

        buildGenesisBlock(blockChainPointer);

        String encryptedBlock = getEncryptedBlock(
                blockChainPointer,
                Key.retrieve(context, chatId, Key.LOCAL_KEY, Key.PRIVATE_KEY));

        return new PendingBlock(chatId, encryptedBlock);
    }

    public PendingBlock buildMessage(String message) {

        buildSentBlock(blockChainPointer, message);

        String encryptedBlock = getEncryptedBlock(
                blockChainPointer,
                Key.retrieve(context, chatId, Key.LOCAL_KEY, Key.PRIVATE_KEY));

        return new PendingBlock(chatId, encryptedBlock);
    }

    public void addPrevSentMessage(PendingBlock block) {

        String plainTextBlock = addPrevSentBlock(
            blockChainPointer,
            block.getEncryptedBlock(),
            Key.retrieve(context, chatId, Key.LOCAL_KEY, Key.PUBLIC_KEY));

        Message newMessage = new Message(plainTextBlock, new Date(0));
        messages.add(newMessage);
    }

    public void addReceivedMessage(StashedBlock block) {

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
    
}
