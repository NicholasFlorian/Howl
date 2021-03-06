package com.teamhowl.howl.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pooled_block_table")
public class PooledBlock {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "chat_id")
    private String chatId;

    @ColumnInfo(name = "version")
    private int version;

    @ColumnInfo(name = "encrypted_block")
    private String encryptedBlock;

    public PooledBlock(String chatId, String encryptedBlock) {

        this.id = -1;
        this.chatId = chatId;
        this.encryptedBlock = encryptedBlock;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public String getChatId() {
        return chatId;
    }

    public int getVersion() {
        return version;
    }

    public String getEncryptedBlock() {
        return encryptedBlock;
    }
}
