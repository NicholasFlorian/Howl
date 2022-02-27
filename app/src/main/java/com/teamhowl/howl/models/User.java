package com.teamhowl.howl.models;

import android.bluetooth.BluetoothDevice;

public class User {

    private String userName;
    private String userId;
    private String chatId;
    private BluetoothDevice device;

    public User(BluetoothDevice device) throws SecurityException{

        this.userName = device.getName();
        this.userId = "USER_ID";
        this.chatId = "CHAT_ID";
        this.device = device;
    }

    public String getUserName(){ return userName; }
    public String getMacAddress() throws SecurityException{ return device.getName(); }
    public String getUserId(){ return userId; }
    public String getChatId(){ return chatId; }
    public BluetoothDevice getDevice(){ return device; }
}
