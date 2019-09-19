package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.entity.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT uuid,sender,receiver,amount,currency,transactionTime FROM transactions WHERE receiver =:userId")
    LiveData<List<Transaction>> getReceivedTransactions(String userId);

    @Query("SELECT uuid,sender,receiver,amount,currency,transactionTime FROM transactions WHERE sender =:userId")
    LiveData<List<Transaction>> getSentTransactionById(String userId);
}
