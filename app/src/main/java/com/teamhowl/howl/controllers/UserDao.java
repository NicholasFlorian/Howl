package com.teamhowl.howl.controllers;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.teamhowl.howl.models.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("SELECT * FROM user_table WHERE chat_id LIKE :chatId")
    public User findUserByChatId(String chatId);

    @Query("SELECT * FROM user_table")
    public List<User> findAllUsers();

    @Query("DELETE FROM user_table WHERE chat_id Like :chatId")
    public void deleteBlocks(String chatId);

}
