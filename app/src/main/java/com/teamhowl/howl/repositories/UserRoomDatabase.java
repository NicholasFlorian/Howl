package com.teamhowl.howl.repositories;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.teamhowl.howl.controllers.UserDao;
import com.teamhowl.howl.models.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class UserRoomDatabase extends RoomDatabase {

    private static UserRoomDatabase INSTANCE;

    public abstract UserDao userDao();

    public static UserRoomDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.getApplicationContext(),
                UserRoomDatabase.class,
                "user_data_base").allowMainThreadQueries().build();
        }

        return INSTANCE;
    }

}