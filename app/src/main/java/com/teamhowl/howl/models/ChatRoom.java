package com.teamhowl.howl.models;

import android.content.Context;

public class ChatRoom {

    private User user;
    private BlockChain blockChain;

    public ChatRoom(User user, Context context){

        this.user = user;
        this.blockChain = new BlockChain(user.getChatId(), context);
    }

    public void refresh(){
        //on database changed (pendingblocks/stashedBlocks?);

    }

    public User getUser(){
        return user;
    }

    public BlockChain getChain(){
        return blockChain;
    }
}
