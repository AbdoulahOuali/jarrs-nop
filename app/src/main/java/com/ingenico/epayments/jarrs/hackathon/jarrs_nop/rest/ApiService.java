package com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest;

import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.Transfer;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.TransferList;
import com.ingenico.epayments.jarrs.hackathon.jarrs_nop.rest.bean.Usr;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/v1/transaction")
    Call<List<Transfer>> getTransactionsFromServer();

    @Headers("Accept: */*")
    @GET("api/v1/user/{userId}")
    Call<Usr> getUserFromServer(@Path("userId") String userId);

    @Headers("Accept: */*")
    @POST("api/v1/transaction/batch-transfer/{userId}")
    Call<String> updateTransactions(@Path("userId") String userId, @Body TransferList transferList);

    @Headers("Accept: */*")
    @POST("api/v1/transaction/transfer/{userId}")
    Call<Transfer> updateSingleTransaction(@Path("userId") String userId, @Body Transfer transfer);


}
