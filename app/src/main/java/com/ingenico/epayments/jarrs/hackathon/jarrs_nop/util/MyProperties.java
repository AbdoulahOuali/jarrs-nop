package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util;

import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.nfc.bean.NfcReceiverMessage;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyProperties {

    private static MyProperties mInstance = null;

    private String loggedInUserId;
    private BigDecimal balance;
    private String firstName;
    private String lastName;
    private String currency = "EUR";
    private NfcReceiverMessage receiverMessage;

    protected MyProperties() {
    }

    public static synchronized MyProperties getInstance() {
        if (null == mInstance) {
            mInstance = new MyProperties();
        }
        return mInstance;
    }
}
