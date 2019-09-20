package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.R;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.ApiService;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.ServiceGenerator;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.Usr;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util.InternetConnection;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util.MyProperties;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button loginButton = findViewById(R.id.login_button);
        final EditText userNameText = findViewById(R.id.user_id_login_et);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUserId = userNameText.getText().toString();
                //getBalanceFromServer(inputUserId, getApplicationContext());
                MyProperties myProperties = MyProperties.getInstance();
                myProperties.setLoggedInUserId(inputUserId);
                goToMainActivity();
            }
        });


    }

    public void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }


//    private void updateUserBalance(String userId) {
//        String balance = getBalanceFromServer(userId, getApplicationContext());
//        MyProperties myProperties = MyProperties.getInstance();
//        myProperties.setBalance(BigDecimal.valueOf(Double.valueOf(balance)));
//        myProperties.setLoggedInUserId(userId);
//    }

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
                        MyProperties myProperties = MyProperties.getInstance();
                        myProperties.setBalance(BigDecimal.valueOf(Double.valueOf(response.body().getBalance())));
                        myProperties.setLoggedInUserId(userId);
                        goToMainActivity();
                    }
                    Log.e(TAG, response.message() + ", gotten from server: " + response.raw());
                }

                @Override
                public void onFailure(Call<Usr> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                    MyProperties myProperties = MyProperties.getInstance();
                    myProperties.setBalance(BigDecimal.valueOf(1000));
                    myProperties.setLoggedInUserId(userId);
                    Log.e(TAG, "Failed to get user from server, setting balance: 1000");
                    goToMainActivity();
                }
            }));


    }


}
