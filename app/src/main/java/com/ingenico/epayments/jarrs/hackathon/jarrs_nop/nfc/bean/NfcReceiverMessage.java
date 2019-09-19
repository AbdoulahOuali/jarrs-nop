package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.nfc.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NfcReceiverMessage {

    private String uuid;

    private String sender;

    private String receiver;

    private String transactionTime;// yyyy-MM-dd HH:mm:ss

    private String amount;

    private String currency;
}
