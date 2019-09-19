package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {

    @SerializedName("uuid")
    @Expose
    private String uuid;

    @SerializedName("fromUserId")
    @Expose
    private String fromUserId;

    @SerializedName("toUserId")
    @Expose
    private String toUserId;

    @SerializedName("transactionTime")
    @Expose
    private String transactionTime;

    @SerializedName("amount")
    @Expose
    private BigDecimal amount;

    @SerializedName("currency")
    @Expose
    private String currency;
}
