package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.util;

import android.content.Context;
import android.util.Log;

import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.ApiService;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.ServiceGenerator;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.Usr;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestUtil {
    private static final String TAG = RestUtil.class.getSimpleName();

    public static String getBalanceFromServer(String userId, Context context) {

        ApiService apiService = ServiceGenerator.createService(ApiService.class);
        if (InternetConnection.checkConnection(context)) {
            Log.e(TAG, "Good internet connection");
        } else {
            Log.e(TAG, "no internet connection");
        }

        final Balance mainBalance = new Balance();

        Call<Usr> call = apiService.getUserFromServer(userId);

        call.enqueue((new Callback<Usr>() {
            @Override
            public void onResponse(Call<Usr> call, Response<Usr> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "http status code: " + response.code());
                    Log.e(TAG, "http error: " + response.errorBody());
                    mainBalance.setValue(response.body().getBalance());
                }
                Log.e(TAG, response.message() + ", gotten from server: " + response.raw());
            }

            @Override
            public void onFailure(Call<Usr> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Log.e(TAG, "Failed to get user from server, setting balance: 1000");
                mainBalance.setValue("1000");
            }
        }));

        int count = 0;

        while(count <10) {
            if(mainBalance.getValue() == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }

        return (mainBalance.getValue() == null) ? "1000" : mainBalance.getValue();
    }

}
