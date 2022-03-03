package com.teamhowl.howl.models;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class PooledBlock {


    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "chat_id")
    private String chatId;

    @ColumnInfo(name = "encrypted_block")
    private String encryptedBlock;
}
