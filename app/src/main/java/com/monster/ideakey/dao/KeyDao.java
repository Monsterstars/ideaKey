package com.monster.ideakey.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.monster.ideakey.entity.IdeaKey;

import java.util.List;

@Dao
public interface KeyDao {

    @Insert
    long insertOneIdeaKey(IdeaKey ideaKey);

    @Query("SELECT * FROM IdeaKey")
    List<IdeaKey> loadAllKeys();

    @Query("SELECT * FROM IdeaKey WHERE id = (:id)")
    IdeaKey loadOneIdeaKey(long id);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateIdeaKey(IdeaKey ideaKey);

    @Delete
    void deleteIdeaKey(IdeaKey ideaKey);

    @Query("DELETE  FROM IdeaKey where id=(:id)")
    void deleteById(long id);

}
