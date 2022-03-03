package com.teamhowl.howl.models;

import android.bluetooth.BluetoothDevice;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey
    @ColumnInfo(name = "chat_id")
    private String chatId;

    @ColumnInfo(name = "user_name")
    private String userName;

    /* Encapsulated for UI */
    private BluetoothDevice device;

    public User(String chatId, String userName){

        this.chatId = chatId;
        this.userName = userName;
        this.device = null;
    }

    public User(BluetoothDevice device) throws SecurityException{

        this.userName = device.getName();
        this.chatId = "CHAT_ID";
        this.device = device;
    }

    public String getChatId(){ return chatId; }
    public String getUserName(){ return userName; }
    public String getMacAddress() throws SecurityException{ return device.getName(); }
    public BluetoothDevice getDevice(){ return device; }
}
