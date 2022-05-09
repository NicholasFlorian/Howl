package com.teamhowl.howl.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.sql.Date;

@Entity(tableName = "stashed_block_table", indices = {@Index(value = {"encrypted_block"}, unique = true)})
public class StashedBlock {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "chat_id")
    private String chatId;

    @ColumnInfo(name = "version")
    private int version;

    @ColumnInfo(name = "encrypted_block")
    private String encryptedBlock;

    @ColumnInfo(name = "time_received")
    private Long timeReceived;


    public StashedBlock(String chatId, String encryptedBlock) {//, Date timeReceived) {

        this.id = id;
        this.chatId = chatId;
        this.encryptedBlock = encryptedBlock;
        this.timeReceived = (long) 0;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setDateTimeReceived(Date timeReceived) {
        this.timeReceived = timeReceived.getTime();
    }

    public void setTimeReceived(Long timeReceived){
        this.timeReceived = timeReceived;
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

    public Date getDateTimeReceived() {
        return new Date(timeReceived);
    }

    public long getTimeReceived() {
        return timeReceived;
    }

}
