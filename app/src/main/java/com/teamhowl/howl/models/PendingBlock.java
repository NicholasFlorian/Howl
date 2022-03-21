package com.teamhowl.howl.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "pending_block_table", indices = {@Index(value = {"encrypted_block"}, unique = true)})
public class PendingBlock {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "chat_id")
    private String chatId;

    @ColumnInfo(name = "version")
    private int version;

    @ColumnInfo(name = "encrypted_block")
    private String encryptedBlock;

    @ColumnInfo(name = "plaintext_block")
    private String plaintextBlock;

    public PendingBlock(String chatId, String plaintextBlock, String encryptedBlock) {

        this.chatId = chatId;
        this.plaintextBlock = plaintextBlock;
        this.encryptedBlock = encryptedBlock;
    }

    @Ignore
    public PendingBlock(String chatId, String plaintextBlock) {

        this.chatId = chatId;
        this.plaintextBlock = plaintextBlock;
        this.encryptedBlock = "";
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setEncryptedBlock(String encryptedBlock) {
        this.encryptedBlock = encryptedBlock;
    }

    public void setPlaintextBlock(String plaintextBlock) {
        this.plaintextBlock = plaintextBlock;
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

    public String getPlaintextBlock() {
        return plaintextBlock;
    }
}
