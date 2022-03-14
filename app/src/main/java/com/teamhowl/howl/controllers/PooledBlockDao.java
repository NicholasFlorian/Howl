package com.teamhowl.howl.controllers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.teamhowl.howl.models.PooledBlock;

import java.util.List;

@Dao
public interface PooledBlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PooledBlock pooledBlock);

    @Query("SELECT * FROM pooled_block_table WHERE chat_id LIKE :chatId ORDER BY version")
    public abstract List<PooledBlock> findBlocksByChatId(String chatId);

    @Query("SELECT * FROM pooled_block_table")
    public abstract List<PooledBlock> findAllBlocks();

    @Query("DELETE FROM pooled_block_table WHERE chat_id Like :chatId")
    public void deleteBlocks(String chatId);

}
