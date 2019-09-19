package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest;

import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.TransferList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    /*
    Retrofit get annotation with our URL
    And our method that will return us the List of ContactList
    */
    @POST("api/v1/transaction/batch-transfer/{userId}")
    Call<String> updateTransactions(@Path("userId") String userId, @Body TransferList transferList);



}
