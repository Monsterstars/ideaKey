package com.monster.ideakey.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.monster.ideakey.dao.KeyDao;
import com.monster.ideakey.entity.IdeaKey;

@Database(entities = {IdeaKey.class},version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract KeyDao keyDao();

}