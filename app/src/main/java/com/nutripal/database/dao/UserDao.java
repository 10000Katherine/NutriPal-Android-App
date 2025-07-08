package com.nutripal.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.LiveData;

import com.nutripal.models.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password LIMIT 1")
    User findByUserCredentials(String email, String password);


    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    LiveData<User> findByEmail(String email);

    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    User findUserByEmailOnce(String email);

    @Update
    void update(User user);
}