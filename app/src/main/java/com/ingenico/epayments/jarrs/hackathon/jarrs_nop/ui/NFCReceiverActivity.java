package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.R;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.AppDatabase;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.entity.Transaction;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.nfc.bean.NfcReceiverMessage;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.nfc.bean.NfcSenderMessage;

import java.math.BigDecimal;
import java.util.Date;

public class NFCReceiverActivity extends AppCompatActivity {

    public static final String MIME_TEXT_PLAIN = "text/plain";

    private static Gson gson = new Gson();
    private final String TAG = NFCReceiverActivity.class.getSimpleName();

    private TextView tvIncomingMessage;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreceiver);

        if (!isNfcSupported()) {
            Toast.makeText(this, "Nfc is not supported on this device", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC disabled on this device. Turn on to proceed", Toast.LENGTH_SHORT).show();
        }

        initViews();
    }

    // need to check NfcAdapter for nullability. Null means no NFC support on the device
    private boolean isNfcSupported() {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return this.nfcAdapter != null;
    }

    private void initViews() {
        this.tvIncomingMessage = findViewById(R.id.tv_in_message);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // also reading NFC message from here in case this activity is already started in order
        // not to start another instance of this activity
        super.onNewIntent(intent);
        receiveMessageFromDevice(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // foreground dispatch should be enabled here, as onResume is the guaranteed place where app
        // is in the foreground
        enableForegroundDispatch(this, this.nfcAdapter);
        receiveMessageFromDevice(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch(this, this.nfcAdapter);
    }

    private void receiveMessageFromDevice(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage inNdefMessage = (NdefMessage) parcelables[0];
            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
            NdefRecord ndefRecord_0 = inNdefRecords[0];

            String inMessage = new String(ndefRecord_0.getPayload());
            this.tvIncomingMessage.setText(inMessage);

            Log.e(TAG, "Received Message: " + inMessage);

            if (inMessage.contains("receiver")) {
                NfcReceiverMessage nfcReceiverMessage = gson.fromJson(inMessage, NfcReceiverMessage.class);
                saveToDatabase(nfcReceiverMessage);
            } else {
                NfcSenderMessage nfcSenderMessage = gson.fromJson(inMessage, NfcSenderMessage.class);
                sendResponse(nfcSenderMessage);
            }
        }
    }

    private void sendResponse(NfcSenderMessage nfcSenderMessage) {
        Log.e(TAG, "Sending response of received message: " + nfcSenderMessage.toString());
        NfcReceiverMessage nfcReceiverMessage = NfcReceiverMessage.builder()
                .uuid(nfcSenderMessage.getUuid())
                .sender(nfcSenderMessage.getSender())
                .receiver("abdoulah")
                .amount(nfcSenderMessage.getAmount())
                .currency(nfcSenderMessage.getCurrency())
                .transactionTime(nfcSenderMessage.getTransactionTime())
                .build();

        Log.e(TAG, "Sending response of original sender: " + nfcReceiverMessage.toString());
    }

    private void saveToDatabase(NfcReceiverMessage nfcReceiverMessage) {

        Log.e(TAG, "Saving message to DB: " + nfcReceiverMessage.toString());
        AppDatabase db = AppDatabase.getAppDBInstance(this.getApplicationContext());

        Transaction transaction = Transaction.builder()
                .uuid(nfcReceiverMessage.getUuid())
                .sender(nfcReceiverMessage.getSender())
                .receiver(nfcReceiverMessage.getReceiver())
                .transactionTime(new Date())
                .amount(BigDecimal.valueOf(Double.valueOf(nfcReceiverMessage.getAmount())))
                .currency(nfcReceiverMessage.getCurrency())
                .build();

        db.TransactionDao().insert(transaction);
        Log.e(TAG, "Message saved to DB: ");
    }


    // Foreground dispatch holds the highest priority for capturing NFC intents
    // then go activities with these intent filters:
    // 1) ACTION_NDEF_DISCOVERED
    // 2) ACTION_TECH_DISCOVERED
    // 3) ACTION_TAG_DISCOVERED

    // always try to match the one with the highest priority, cause ACTION_TAG_DISCOVERED is the most
    // general case and might be intercepted by some other apps installed on your device as well

    // When several apps can match the same intent Android OS will bring up an app chooser dialog
    // which is undesirable, because user will most likely have to move his device from the tag or another
    // NFC device thus breaking a connection, as it's a short range

    public void enableForegroundDispatch(AppCompatActivity activity, NfcAdapter adapter) {

        // here we are setting up receiving activity for a foreground dispatch
        // thus if activity is already started it will take precedence over any other activity or app
        // with the same intent filters


        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException ex) {
            throw new RuntimeException("Check your MIME type");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public void disableForegroundDispatch(final AppCompatActivity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
}
