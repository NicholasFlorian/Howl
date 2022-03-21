package com.teamhowl.howl.controllers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.teamhowl.howl.models.PendingBlock;

import java.util.List;

@Dao
public interface PendingBlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PendingBlock pendingBlock);

    @Query("SELECT * FROM pending_block_table WHERE chat_id LIKE :chatId ORDER BY id")
    public List<PendingBlock> findBlocksByChatId(String chatId);

    @Query("DELETE FROM pending_block_table WHERE chat_id Like :chatId")
    public void deleteBlocks(String chatId);

}
