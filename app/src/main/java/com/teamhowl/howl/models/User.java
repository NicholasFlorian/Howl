package com.teamhowl.howl.models;

public class User {

    private String userName;
    private String macAddress;
    private String userId;
    private String chatId;

    public User(String userName, String chatId){

        this.userName = userName;
        this.chatId = chatId;
    }

    public User(String userName, String localMacAddress, String foreignMacAddress){

        this.userName = userName;
        this.macAddress = foreignMacAddress;
        this.userId = "";
        this.chatId = "";
    }

    public String getUserName(){ return userName; }
    public String getMacAddress(){ return macAddress; }
    public String getUserId(){ return userId; }
    public String getChatId(){ return chatId; }
}
