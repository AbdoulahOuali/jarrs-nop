package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean;

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

    private String uuid;
    private String fromUserId;
    private String toUserId;
    private String transactionTime;
    private BigDecimal amount;
    private String currency;
}
