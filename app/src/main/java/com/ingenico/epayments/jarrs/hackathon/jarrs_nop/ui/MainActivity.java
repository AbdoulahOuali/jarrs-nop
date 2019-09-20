package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.R;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.AppDatabase;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.database.entity.Transaction;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.ApiService;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.ServiceGenerator;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.Transfer;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.TransferList;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.Usr;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util.CustomDateFormatter;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util.InternetConnection;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util.MyProperties;

import org.fabiomsr.moneytextview.MoneyTextView;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "On create MainActivity ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyProperties myProperties = MyProperties.getInstance();

        final TextView welcomeUserId = findViewById(R.id.welcome_user_home_textView);
        welcomeUserId.setText(getString(R.string.welcome_user_string, MyProperties.getInstance().getLoggedInUserId()));

        //final MoneyTextView moneyTextView = findViewById(R.id.money_user_textView);
        getBalanceFromServer(myProperties.getLoggedInUserId(), getApplicationContext());

        final Button addMoneyButton = findViewById(R.id.add_money_button);
        final Button payButton = findViewById(R.id.pay_button);
        final Button receiveFundsButton = findViewById(R.id.receive_button);
        final Button showTransactions = findViewById(R.id.show_transactions_button);

        addMoneyButton.setOnClickListener(this);
        payButton.setOnClickListener(this);
        receiveFundsButton.setOnClickListener(this);
        showTransactions.setOnClickListener(this);

        // updateTransactions();
//        updateSingleTransactionOnServer();

//        testDatabase();
    }


    public void getBalanceFromServer(String userId, Context context) {

        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        if (InternetConnection.checkConnection(context)) {
            Log.e(TAG, "Good internet connection");
        } else {
            Log.e(TAG, "no internet connection");
        }

        Call<Usr> call = apiService.getUserFromServer(userId);

        call.enqueue((new Callback<Usr>() {
            @Override
            public void onResponse(Call<Usr> call, Response<Usr> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "http status code: " + response.code());
                    Log.e(TAG, "http error: " + response.errorBody());

                }
                MyProperties myProperties = MyProperties.getInstance();
                myProperties.setFirstName(response.body().getFirstName());
                myProperties.setLastName(response.body().getLastName());
                myProperties.setBalance(BigDecimal.valueOf(Double.valueOf(response.body().getBalance())));

                final TextView welcomeUserId = findViewById(R.id.welcome_user_home_textView);
                welcomeUserId.setText(getString(R.string.welcome_user_string, MyProperties.getInstance().getFirstName()));

                final MoneyTextView moneyTextView = findViewById(R.id.money_user_textView);
                moneyTextView.setAmount(MyProperties.getInstance().getBalance().floatValue(), "€");

                Log.e(TAG, response.message() + ", gotten from server: " + response.raw());
            }

            @Override
            public void onFailure(Call<Usr> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                MyProperties myProperties = MyProperties.getInstance();
                myProperties.setBalance(BigDecimal.valueOf(1000));
                myProperties.setLoggedInUserId(userId);
                myProperties.setFirstName(userId);
                myProperties.setLastName(userId);

                final TextView welcomeUserId = findViewById(R.id.welcome_user_home_textView);
                welcomeUserId.setText(getString(R.string.welcome_user_string, MyProperties.getInstance().getFirstName()));

                final MoneyTextView moneyTextView = findViewById(R.id.money_user_textView);
                moneyTextView.setAmount(MyProperties.getInstance().getBalance().floatValue(), "€");

                Log.e(TAG, "Failed to get user from server, setting balance: 1000");
            }
        }));


    }


    private void testDatabase() {
        AppDatabase db = AppDatabase.getAppDBInstance(this.getApplicationContext());

        Transaction transaction1 = Transaction.builder()
                .uuid(UUID.randomUUID().toString())
                .sender("sandip")
                .receiver("abdoulah")
                .amount(BigDecimal.TEN)
                .currency("EUR")
                .synchronizedOnline(false)
                .transactionTime(new Date())
                .build();

        db.TransactionDao().insert(transaction1);

        Transaction transaction2 = db.TransactionDao().getTransactionByUuid(transaction1.getUuid());

        Log.e(TAG, transaction2.toString());

        transaction2.setSynchronizedOnline(true);
        db.TransactionDao().update(transaction2);


        Transaction transaction3 = db.TransactionDao().getTransactionByUuid(transaction1.getUuid());

        Log.e(TAG, transaction3.toString());
    }

    private void updateSingleTransactionOnServer() {
        Transfer transfer = Transfer.builder()
                .uuid(UUID.randomUUID().toString())
                .fromUserId("abdoulah")
                .toUserId("sandip")
                .amount(BigDecimal.valueOf(50))
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

    private void getTransactionsFromServer() {
        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        if (InternetConnection.checkConnection(getApplicationContext())) {
            Log.e(TAG, "Good internet connection");
        } else {
            Log.e(TAG, "no internet connection");
        }


        Call<List<Transfer>> call = apiService.getTransactionsFromServer();


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

        if (InternetConnection.checkConnection(getApplicationContext())) {
            Log.e(TAG, "Good internet connection");
        } else {
            Log.e(TAG, "no internet connection");
        }

        Call<String> call = apiService.updateTransactions("sandip", transferList);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "response succes");
                } else {
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.e(TAG, "on post resume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "on start");
        MyProperties myProperties = MyProperties.getInstance();
        getBalanceFromServer(myProperties.getLoggedInUserId(), getApplicationContext());
    }


}
