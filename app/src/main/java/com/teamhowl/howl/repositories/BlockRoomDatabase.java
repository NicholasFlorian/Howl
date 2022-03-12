package com.teamhowl.howl.repositories;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.teamhowl.howl.controllers.PendingBlockDao;
import com.teamhowl.howl.controllers.PooledBlockDao;
import com.teamhowl.howl.controllers.StashedBlockDao;
import com.teamhowl.howl.models.PendingBlock;
import com.teamhowl.howl.models.PooledBlock;
import com.teamhowl.howl.models.StashedBlock;


@Database(
    entities = {PendingBlock.class, PooledBlock.class, StashedBlock.class},
    version = 1,
    exportSchema = false)
public abstract class BlockRoomDatabase extends RoomDatabase {

    private static BlockRoomDatabase INSTANCE;

    public abstract PendingBlockDao pendingBlockDao();
    public abstract PooledBlockDao pooledBlockDao();
    public abstract StashedBlockDao stashedBlockDao();

    public static BlockRoomDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                context.getApplicationContext(),
                BlockRoomDatabase.class,
                "block_data_base").build();
        }

        return INSTANCE;
    }

}
