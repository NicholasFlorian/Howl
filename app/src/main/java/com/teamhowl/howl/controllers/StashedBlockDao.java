package com.teamhowl.howl.controllers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.teamhowl.howl.models.StashedBlock;

import java.util.List;

@Dao
public interface StashedBlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(StashedBlock stashedBlock);

    @Query("SELECT * FROM stashed_block_table WHERE chat_id LIKE :chatId ORDER BY id")
    public List<StashedBlock> findBlocksByChatId(String chatId);

    @Query("SELECT * FROM stashed_block_table WHERE encrypted_block LIKE :encryptedBlock LIMIT 1")
    public StashedBlock getBlock(String encryptedBlock);

    @Query("DELETE FROM stashed_block_table WHERE chat_id Like :chatId")
    public void deleteBlocks(String chatId);

}
