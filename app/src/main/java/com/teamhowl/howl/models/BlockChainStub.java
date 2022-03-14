package com.teamhowl.howl.models;

import android.content.Context;
import android.util.Log;

import com.teamhowl.howl.controllers.PendingBlockDao;
import com.teamhowl.howl.controllers.StashedBlockDao;
import com.teamhowl.howl.repositories.BlockRoomDatabase;
import com.teamhowl.howl.utilities.Key;

import java.sql.Date;
import java.util.ArrayList;

public class BlockChainStub {
    private final String TAG = "HOWL :: BlockChainSTUB:";
    private final String TAG_JNI = "HOWL :: BlockChainSTUB :: JNI:";

    private String chatId;
    private Context context;
    private BlockRoomDatabase blockRoomDatabase;
    private ArrayList<Message> messages;

    public BlockChainStub (Context context, String chatId) {
        Log.d(TAG, "Construct BlockChain");

        this.chatId = chatId;
        this.context = context;
        this.blockRoomDatabase = BlockRoomDatabase.getDatabase(context);
        this.messages = new ArrayList<>();
    }

    public void refresh(){
        Log.d(TAG, "Refresh");

        PendingBlockDao pendingBlockDao = blockRoomDatabase.pendingBlockDao();
        StashedBlockDao stashedBlockDao = blockRoomDatabase.stashedBlockDao();

        this.messages.clear();

        for(PendingBlock block : pendingBlockDao.findBlocksByChatId(chatId))
            addPrevSentMessage(block);

        for(StashedBlock block : stashedBlockDao.findBlocksByChatId(chatId))
            addReceivedMessage(block);
    }

    public void destroy(){

    }

    public PendingBlock buildGenesisMessage(){

        Log.d(TAG, "Build Genesis Message");
        String encryptedBlock = "GENSISBLOCK";

        return new PendingBlock(chatId, encryptedBlock);
    }

    public PendingBlock buildMessage(String message) {

        Log.d(TAG, "Build Genesis Message: " + message);

        String encryptedBlock = message;

        return new PendingBlock(chatId, encryptedBlock);
    }

    public void addPrevSentMessage(PendingBlock block) {

        Log.d(TAG, "Add Previously Sent Message");
        Log.d(TAG_JNI, "addPrevSentBlock()");
        String plainTextBlock = block.getEncryptedBlock();

        Message newMessage = new Message(plainTextBlock, new Date(0));
        messages.add(newMessage);
    }

    public void addReceivedMessage(StashedBlock block) {

        Log.d(TAG, "Add Received Message");
        Log.d(TAG_JNI, "addReceivedBlock()");
        String plainTextBlock = block.getEncryptedBlock();

        Message newMessage = new Message(plainTextBlock, new Date(0), new Date(0));
        messages.add(newMessage);
    }

    public ArrayList<Message> getMessages() {

        return messages;
    }

}
