package com.teamhowl.howl.models;

import androidx.room.Dao;

import java.sql.Date;
import java.util.ArrayList;

public class BlockChain {

    private Long blockChainPointer;

    private String chatId;
    private ArrayList<Message> messages;

    public BlockChain(String chatId) {

        this.chatId = chatId;
        this.messages = new ArrayList<>();
    }

    public void refresh(){


    }

    public void destroy(){

    }

    public void checkForNewMessages(){

    }

    public void addSentMessage(PendingBlock block) throws SecurityException {

        Message newMessage = new Message(block.getEncryptedBlock(), new Date(0));
        messages.add(newMessage);
    }

    public void addReceivedMessage(StashedBlock block) throws SecurityException {

        Message newMessage = new Message(block.getEncryptedBlock(), new Date(0), new Date(0));
        messages.add(newMessage);
    }

    public void checkNewMessage(PooledBlock block) throws SecurityException {

        refresh();


    }
    
}
