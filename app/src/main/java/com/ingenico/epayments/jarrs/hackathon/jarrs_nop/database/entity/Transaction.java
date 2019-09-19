package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
@Entity(tableName = "transactions")
public class Transaction {

    @PrimaryKey
    private Long id;

    @NonNull
    private String uuid;

    @NonNull
    @Size(min = 1, max = 100)
    private String sender;

    @NonNull
    @Size(min = 1, max = 100)

    private String receiver;

    @NonNull
    private Date transactionTime;

    @NonNull
    private BigDecimal amount;

    @NonNull
    @Size(min = 3, max = 3)
    private String currency;

    private boolean synchronizedOnline;
}
