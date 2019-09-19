package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.R;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.ApiService;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.ServiceGenerator;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.Transfer;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.TransferList;

import org.fabiomsr.moneytextview.MoneyTextView;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MoneyTextView moneyTextView = findViewById(R.id.money_user_textView);
        final Button addMoneyButton = findViewById(R.id.add_money_button);
        final Button payButton = findViewById(R.id.pay_button);
        final Button receiveFundsButton = findViewById(R.id.receive_button);
        final Button showTransactions = findViewById(R.id.show_transactions_button);

        addMoneyButton.setOnClickListener(this);
        payButton.setOnClickListener(this);
        receiveFundsButton.setOnClickListener(this);
        showTransactions.setOnClickListener(this);

        updateTransactions();
    }

    private void updateTransactions() {
        TransferList transferList = TransferList.builder()
                .transferList(Arrays.asList(Transfer.builder()
                        .uuid(UUID.randomUUID().toString())
                        .fromUserId("sandip")
                        .toUserId("abdoulah")
                        .amount(BigDecimal.TEN)
                        .currency("EUR")
                        .transactionTime("2019-09-19 10:23:45")
                        .build()))
                .build();
        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        Log.e(TAG, "Start calling method updateTransactions");
        Call<Void> call = apiService.updateTransactions("sandip", transferList);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "response succes");
                } else {
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
        Log.e(TAG, "exit");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_money_button:
                Toast toastMoneyAdded = Toast.makeText(this, "MONEY WAS ADDED", Toast.LENGTH_LONG);
                toastMoneyAdded.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 24);
                toastMoneyAdded.show();
                break;
            case R.id.pay_button:
                Intent payIntent = new Intent(this, NFCSenderActivity.class);
                startActivity(payIntent);
                break;
            case R.id.receive_button:
                Intent receiveIntent = new Intent(this, NFCReceiverActivity.class);
                startActivity(receiveIntent);
                break;
            case R.id.show_transactions_button:
//                Intent intent = new Intent(this,ShowTransactionActivity.class);
//                startActivity(intent);
                Toast toastFundsReceived = Toast.makeText(this, "LOAD TRANSACTIONS ViewModel", Toast.LENGTH_LONG);
                toastFundsReceived.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 24);
                toastFundsReceived.show();
                break;
        }
    }
}
