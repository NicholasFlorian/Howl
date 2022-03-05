package com.teamhowl.howl.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class PendingBlock {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "chat_id")
    private String chatId;

    @ColumnInfo(name = "encrypted_block")
    private String encryptedBlock;


    public PendingBlock(String chatId, String encryptedBlock) {

        this.id = -1;
        this.chatId = chatId;
        this.encryptedBlock = encryptedBlock;
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
}
