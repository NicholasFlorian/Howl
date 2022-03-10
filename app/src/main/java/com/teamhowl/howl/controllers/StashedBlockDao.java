package com.teamhowl.howl.controllers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import com.teamhowl.howl.models.StashedBlock;

import java.util.List;

public interface StashedBlockDao extends Dao {
    @Query("SELECT * FROM StashedBlock WHERE chat_id LIKE :chatId")
    public abstract List<StashedBlock> findBlocksByChatId(String chatId);

    @Delete
    public void deleteBlocks(List<StashedBlock> chatId_blocks);
}
