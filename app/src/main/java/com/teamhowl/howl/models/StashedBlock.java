package com.teamhowl.howl.models;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Date;

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
}
