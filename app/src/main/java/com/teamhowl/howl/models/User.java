package com.teamhowl.howl.models;

public class User {

    private String userName;
    private String chatId;

    public User(String userName, String chatId){

        this.userName = userName;
        this.chatId = chatId;
    }

    public User(String userName, String personalUserId, String foreignUserId){

        this.userName = userName;
        this.chatId = ""; //TODO Hash the combination of IDs
    }

    public String getUserName(){ return userName; }
    public String getChatId(){ return chatId; }
}
