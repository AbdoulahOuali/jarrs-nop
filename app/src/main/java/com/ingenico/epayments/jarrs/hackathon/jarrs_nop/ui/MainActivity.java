package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.R;

import org.fabiomsr.moneytextview.MoneyTextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
