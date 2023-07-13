package com.hein.home;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hein.entity.User;

import java.util.List;

@androidx.room.Dao
public interface Dao {
    @Insert
    void insert(User model);

    @Update
    void update(User model);

    @Delete
    void delete(User model);

    @Query("SELECT * FROM user")
    List<User> getAll();
}
