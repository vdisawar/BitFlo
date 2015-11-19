package io.bitflo.android.api;

import java.util.List;

import io.bitflo.android.models.Account;
import io.bitflo.android.models.Balance;
import io.bitflo.android.models.CardItem;
import io.bitflo.android.models.EmptyObject;
import io.bitflo.android.models.Pay;
import io.bitflo.android.models.Transactions;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 *
 * Created by Vishal on 10/25/15.
 */
public interface BitFloAPI {

    @PUT("/users/account")
    void updateAccount(@Body Account card, Callback<String> cb);

    @POST("/users/transactions/{id}")
    void postTransaction(@Path("id") String userID, @Body Pay payment, Callback<String> cb);

    @POST("/users/withdraw")
    void withdraw(@Body EmptyObject obj, Callback<String> cb);

    @GET("/users/balance")
    void getBalance(Callback<Balance> cb);

    @GET("/users/transactions")
    void getTransactions(Callback<Transactions> cb);
}
