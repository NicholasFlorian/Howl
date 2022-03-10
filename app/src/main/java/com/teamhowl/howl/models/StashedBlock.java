package com.teamhowl.howl.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class StashedBlock {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "chat_id")
    private String chatId;

    @ColumnInfo(name = "encrypted_block")
    private String encryptedBlock;

    @ColumnInfo(name = "time_received")
    private Date timeReceived;


    public StashedBlock(String chatId, String encryptedBlock, Date timeReceived) {

        this.id = id;
        this.chatId = chatId;
        this.encryptedBlock = encryptedBlock;
        this.timeReceived = timeReceived;
    }

    public int getId() {
        return id;
    }

    public String getChatId() {
        return chatId;
    }

    public String getEncryptedBlock() {
        return encryptedBlock;
    }

    public Date getTimeReceived() {
        return timeReceived;
    }

}
