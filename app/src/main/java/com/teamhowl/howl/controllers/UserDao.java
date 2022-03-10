package com.teamhowl.howl.controllers;

import androidx.room.Dao;
import androidx.room.Query;

import com.teamhowl.howl.models.StashedBlock;
import com.teamhowl.howl.models.User;

import java.util.List;

public interface UserDao extends Dao {
    @Query("SELECT * FROM User WHERE chat_id LIKE :chatId")
    public abstract User findUserByChatId(String chatId);

}
