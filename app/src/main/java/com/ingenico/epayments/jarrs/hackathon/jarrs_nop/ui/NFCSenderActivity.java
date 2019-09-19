package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.ui;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.R;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.nfc.OutcomingNfcManager;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.nfc.bean.NfcSenderMessage;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util.CustumDateFormatter;

import java.util.UUID;

public class NFCSenderActivity extends AppCompatActivity implements OutcomingNfcManager.NfcActivity {

    private final String TAG = NFCSenderActivity.class.getSimpleName();

    private TextView tvOutcomingMessage;
    private EditText etOutcomingMessage;
    private Button btnSetOutcomingMessage;

    private NfcAdapter nfcAdapter;
    private OutcomingNfcManager outcomingNfccallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcsender);

        if (!isNfcSupported()) {
            Toast.makeText(this, "Nfc is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC disabled on this device. Turn on to proceed", Toast.LENGTH_SHORT).show();
        }

        initViews();

        // encapsulate sending logic in a separate class
        this.outcomingNfccallback = new OutcomingNfcManager(this);
        this.nfcAdapter.setOnNdefPushCompleteCallback(outcomingNfccallback, this);
        this.nfcAdapter.setNdefPushMessageCallback(outcomingNfccallback, this);
    }

    private void initViews() {
        this.tvOutcomingMessage = findViewById(R.id.tv_out_message);
        this.etOutcomingMessage = findViewById(R.id.et_message);
        this.btnSetOutcomingMessage = findViewById(R.id.btn_set_out_message);
        this.btnSetOutcomingMessage.setOnClickListener((v) -> setOutGoingMessage());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private boolean isNfcSupported() {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return this.nfcAdapter != null;
    }

    private void setOutGoingMessage() {
        NfcSenderMessage nfcSenderMessage = NfcSenderMessage.builder()
                .uuid(UUID.randomUUID().toString())
                .sender("sandip")
                .amount(etOutcomingMessage.getText().toString())
                .currency("EUR")
                .transactionTime(CustumDateFormatter.getCurrentTime())
                .build();

        Gson gson = new Gson();
        String outMessage = gson.toJson(nfcSenderMessage);
        Log.e(TAG, "sending message: " + outMessage);
        this.tvOutcomingMessage.setText(outMessage);
        Log.e(TAG, "Message sent: " + outMessage);
    }

    @Override
    public String getOutcomingMessage() {
        return this.tvOutcomingMessage.getText().toString();
    }

    @Override
    public void signalResult() {
        // this will be triggered when NFC message is sent to a device.
        // should be triggered on UI thread. We specify it explicitly
        // cause onNdefPushComplete is called from the Binder thread
        runOnUiThread(() ->
                Toast.makeText(NFCSenderActivity.this, R.string.message_beaming_complete, Toast.LENGTH_SHORT).show());
    }
}
