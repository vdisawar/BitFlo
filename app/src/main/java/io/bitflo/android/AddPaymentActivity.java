package io.bitflo.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import io.bitflo.android.api.BitFloAPI;
import io.bitflo.android.models.Account;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);

        final EditText cardNumET = (EditText) findViewById(R.id.input_card_number);
        final EditText cvcNumET = (EditText) findViewById(R.id.input_cvc);
        final EditText monthET = (EditText) findViewById(R.id.input_month);
        final EditText yearET = (EditText) findViewById(R.id.input_year);

        Button button = (Button) findViewById(R.id.btn_add_payment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String cardNum = cardNumET.getText().toString();
                String cvc = cvcNumET.getText().toString();
                String month = monthET.getText().toString();
                String year = yearET.getText().toString();

                Card card = new Card(cardNum, Integer.parseInt(month), Integer.parseInt(year), cvc);

                try {
                    Stripe stripe = new Stripe(Constants.STRIPE_KEY);
                    stripe.createToken(card, new TokenCallback() {
                        @Override
                        public void onError(Exception e) {
                            Log.e("Stripe error: ", e.getLocalizedMessage());
                        }

                        @Override
                        public void onSuccess(Token token) {

                            Log.d("Stripe success: ", token.toString());

                            String stripeID = token.getId();
                            Card stripeCard = token.getCard();

                            String first8 = cardNum.substring(0, 9);
                            String last4 = cardNum.substring(cardNum.length() - 4, cardNum.length());
                            // convert to integer array
                            int[]  number = new int[first8.length()];
                            for (int i = 0; i < first8.length(); i++) {
                                number[i] = first8.charAt(i);
                            }

                            String brand = "BitFlo Card";
                            boolean determined = false;
                            if (number[0] == 4){
                                brand = "Visa";
                                determined = true;
                            }

                            if(!determined && number[0] == 3){
                                if(number[1] == 4 || number[1] == 7){
                                    brand = "American Express";
                                    determined = true;
                                }
                            }

                            if(!determined && number[0] == 3){
                                if(number[1] == 0 || number[1] == 6 || number[1] == 8){
                                    brand = "Diners Club";
                                    determined = true;
                                }
                            }

                            if(!determined && number[0] == 5){
                                if(number[1] >= 1 && number[1] <= 5){
                                    brand = "MasterCard";
                                    determined = true;
                                }
                            }

                            if(!determined){
                                if(number[0] == 2){
                                    if(number[1] == 0){
                                        if(number[2] == 1){
                                            if(number[3] == 4){
                                                brand = "enRoute";
                                                determined = true;
                                            }
                                        }
                                    } else if(number[1] == 1){
                                        if(number[2] == 4){
                                            if(number[3] == 9){
                                                brand = "enRoute";
                                                determined = true;
                                            }
                                        }
                                    }
                                }
                            }

                            if(!determined){
                                int discover = Integer.parseInt(first8);
                                if((discover >= 60110000 && discover <= 60119999) ||
                                        (discover >= 65000000 && discover <= 65999999) ||
                                        (discover >= 62212600 && discover <= 62292599)){
                                    brand = "Discover";
                                    determined = true;
                                }
                            }

                            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AddPaymentActivity.this);
                            prefs.edit()
                                    .putString(Constants.CARD_NUMBER, cardNum)
                                    .putString(Constants.CARD_BRAND, brand)
                                    .apply();


                            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                                @Override
                                public void intercept(RequestFacade request) {
                                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                    if (accessToken != null && !accessToken.isExpired())
                                        request.addHeader("X-Facebook-Token", accessToken.getToken());
                                }
                            };

                            RestAdapter adapter = new RestAdapter.Builder()
                                    .setEndpoint(Constants.ENDPOINT)
                                    .setRequestInterceptor(requestInterceptor)
                                    .build();

                            final BitFloAPI api = adapter.create(BitFloAPI.class);
                            Account account = new Account();
                            account.card = stripeID;
                            api.updateAccount(account, new Callback<String>() {
                                @Override
                                public void success(String s, Response response) {
                                    Log.d("/users/account", Integer.toString(response.getStatus()));
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    if (error.getResponse() != null) {
                                        Log.e("/users/account", Integer.toString(error.getResponse().getStatus()));
                                    }
                                }
                            });

                            startActivity(new Intent(AddPaymentActivity.this, ProfileActivity.class));
                            finish();
                        }
                    });
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
