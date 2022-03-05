package com.teamhowl.howl.models;

import java.sql.Date;
import java.util.ArrayList;

public class BlockChain {

    private String chatId;
    private ArrayList<Message> messages;

    public BlockChain(String chatId) {

        this.chatId = chatId;
        this.messages = new ArrayList<>();
    }

    public void addSentMessage(PendingBlock block) throws SecurityException {

        Message newMessage = new Message(block.getEncryptedBlock(), new Date(0));
        messages.add(newMessage);
    }

    public void addReceivedMessage(StashedBlock block) throws SecurityException {

        Message newMessage = new Message(block.getEncryptedBlock(), new Date(0), new Date(0));
        messages.add(newMessage);
    }
}
