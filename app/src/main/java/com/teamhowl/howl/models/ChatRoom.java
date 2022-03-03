package com.teamhowl.howl.models;

public class ChatRoom {

    private User user;
    private BlockChain blockChain;

    //public ChatRoom(String chatId){
    //    this.user = User(chatId);
    //}

    public void refresh(){}
    public BlockChain getChain(){ return blockChain; }
}
