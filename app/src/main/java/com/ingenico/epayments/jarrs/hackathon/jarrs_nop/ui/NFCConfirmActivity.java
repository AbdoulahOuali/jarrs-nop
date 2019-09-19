package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.R;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.nfc.OutcomingNfcManager;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.nfc.bean.NfcReceiverMessage;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util.MyProperties;

public class NFCConfirmActivity extends AppCompatActivity implements OutcomingNfcManager.NfcActivity {

    private final String TAG = NFCConfirmActivity.class.getSimpleName();

    private Gson gson = new Gson();

    final Button confirmNFCtransaction = findViewById(R.id.confirm_button);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcconfirm);

        final Button confirmNFCtransaction = findViewById(R.id.confirm_button);
        confirmNFCtransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyProperties myProperties = MyProperties.getInstance();
                NfcReceiverMessage nfcReceiverMessage = myProperties.getReceiverMessage();

                String outMessage = gson.toJson(nfcReceiverMessage);
            }
        });

    }

    @Override
    public String getOutcomingMessage() {
        return null;
    }

    @Override
    public void signalResult() {

    }
}
