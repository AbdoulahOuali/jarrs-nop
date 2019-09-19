package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.entity.User;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    LiveData<User> findByName(String first, String last);

    @Query("SELECT * FROM users WHERE user_id =:usrId")
    LiveData<User> findById(String usrId);
}
