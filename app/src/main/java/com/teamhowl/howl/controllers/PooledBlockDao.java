package com.teamhowl.howl.controllers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import com.teamhowl.howl.models.StashedBlock;

import java.util.List;

public interface PooledBlockDao extends Dao {
    @Query("SELECT * FROM StashedBlock WHERE chat_id LIKE :chatId")
    public abstract List<StashedBlock> findBlocksByChatId(String chatId);

    @Query("SELECT * FROM StashedBlock")
    public abstract List<StashedBlock> findAllBlocks();

    @Delete
    public void deleteBlocks(List<StashedBlock> chatId_blocks);
}
