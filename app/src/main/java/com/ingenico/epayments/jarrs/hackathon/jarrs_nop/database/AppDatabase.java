package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.converter.BigDecimalConverter;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.converter.DateConverter;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.dao.TransactionDao;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.dao.UserDao;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.entity.Transaction;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.entity.User;

@Database(entities = {User.class, Transaction.class}, version = 1)
@TypeConverters({DateConverter.class, BigDecimalConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    //singleton db instance
    private static AppDatabase appDBInstance;

    public abstract UserDao userDao();

    public abstract TransactionDao TransactionDao();

    /**
     * builds a singleton database instance, Allows Room to destructively recreate database tables if migrations are not
     * available when downgrading to old schema versions.
     */
    public static synchronized AppDatabase getAppDBInstance(Context context) {
        if (appDBInstance == null) {
            appDBInstance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return appDBInstance;
    }
}