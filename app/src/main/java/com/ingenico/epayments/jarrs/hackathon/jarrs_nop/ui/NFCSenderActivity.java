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
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.ApiService;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.ServiceGenerator;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.Transfer;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util.CustomDateFormatter;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util.InternetConnection;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util.MyProperties;

import java.math.BigDecimal;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        String amount = etOutcomingMessage.getText().toString();

        NfcSenderMessage nfcSenderMessage = NfcSenderMessage.builder()
                .uuid(UUID.randomUUID().toString())
                .sender(MyProperties.getInstance().getLoggedInUserId())
                .amount(amount)
                .currency("EUR")
                .transactionTime(CustomDateFormatter.getCurrentTime())
                .build();

        MyProperties myProperties = MyProperties.getInstance();

        if ( Double.valueOf(amount) > myProperties.getBalance().doubleValue() ) {
            Log.e(TAG, "Insufficient Balance" );
            this.tvOutcomingMessage.setText("Insufficient Balance");
        } else {
            this.tvOutcomingMessage.setText("Sending € "+amount);
            Gson gson = new Gson();
            String outMessage = gson.toJson(nfcSenderMessage);
            Log.e(TAG, "sending message: " + outMessage);
            updateSingleTransactionOnServer(nfcSenderMessage.getSender(), nfcSenderMessage.getAmount());
            Log.e(TAG, "Message sent: " + outMessage);
        }

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


    private void updateSingleTransactionOnServer(String userId, String amount) {

        String receiver = (userId == "sandip") ? "abdoulah" : "sandip";

        Transfer transfer = Transfer.builder()
                .uuid(UUID.randomUUID().toString())
                .fromUserId(userId)
                .toUserId(receiver)
                .amount(BigDecimal.valueOf(Double.valueOf(amount)))
                .currency("EUR")
                .transactionTime(CustomDateFormatter.getCurrentTime())
                .build();
        Log.e(TAG, "created transaction: " + transfer.toString());

        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        Log.e(TAG, "Start calling method updateSingleTransactionOnServer");

        if (InternetConnection.checkConnection(getApplicationContext())) {
            Log.e(TAG, "Good internet connection");
        } else {
            Log.e(TAG, "no internet connection");
        }

        Call<Transfer> call = apiService.updateSingleTransaction(transfer.getFromUserId(), transfer);
        call.enqueue(new Callback<Transfer>() {
            @Override
            public void onResponse(Call<Transfer> call, Response<Transfer> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "http status code: " + response.code());
                    Log.e(TAG, "http error: " + response.errorBody());
                    return;
                }
                Log.e(TAG, response.message() + ", posted to server: " + response.raw());

            }

            @Override
            public void onFailure(Call<Transfer> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
