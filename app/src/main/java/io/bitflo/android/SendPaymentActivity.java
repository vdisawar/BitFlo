package io.bitflo.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Rfc822Tokenizer;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.bitflo.android.api.BitFloAPI;
import io.bitflo.android.models.Pay;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SendPaymentActivity extends AppCompatActivity {

    private TextView currentPrice;
    private AutoCompleteTextView friends;
    private HashMap<String, String> friendsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Fade());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF4081")));
            actionBar.setTitle("Send Money");
            getWindow().setStatusBarColor(Color.parseColor("#FF4081"));
        }

        Log.d("FB token", AccessToken.getCurrentAccessToken().getToken());
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading. Please Wait.");
        progress.setCancelable(false);
        progress.show();

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code

                        try {
                            Log.d("YO", "REached");

                            friendsArray = new HashMap<>();
                            JSONObject friendsJson = object.getJSONObject("friends");
                            JSONArray friendData = friendsJson.getJSONArray("data");
                            if (friendData != null) {
                                for (int i = 0; i < friendData.length(); i++) {
                                    friendsArray.put(friendData.getJSONObject(i).getString("name"),
                                            friendData.getJSONObject(i).getString("id"));
                                }
                            }

                            Log.d("JSON", response.getJSONObject().toString());

                            friends = (AutoCompleteTextView) findViewById(R.id.choose_friends);

                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<String>(SendPaymentActivity.this,
                                            android.R.layout.simple_dropdown_item_1line,
                                            friendsArray.keySet().toArray(new String[friendsArray.keySet().size()]));
                            friends.setAdapter(adapter);
                            friends.setThreshold(0);

                            friends.setOnTouchListener(new View.OnTouchListener() {

                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    friends.showDropDown();
                                    return false;
                                }
                            });

                            progress.cancel();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture,friends");
        request.setParameters(parameters);
        request.executeAsync();


        currentPrice = (TextView) findViewById(R.id.amount_view);

        Button one = (Button) findViewById(R.id.button_1);
        Button two = (Button) findViewById(R.id.button_2);
        Button three = (Button) findViewById(R.id.button_3);
        Button four = (Button) findViewById(R.id.button_4);
        Button five = (Button) findViewById(R.id.button_5);
        Button six = (Button) findViewById(R.id.button_6);
        Button seven = (Button) findViewById(R.id.button_7);
        Button eight = (Button) findViewById(R.id.button_8);
        Button nine = (Button) findViewById(R.id.button_9);
        Button period = (Button) findViewById(R.id.button_period);
        Button zero = (Button) findViewById(R.id.button_0);
        ImageButton delete = (ImageButton) findViewById(R.id.button_delete);

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().concat("1"));
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().concat("2"));
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().concat("3"));
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().concat("4"));
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().concat("5"));
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().concat("6"));
            }
        });
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().concat("7"));
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().concat("8"));
            }
        });
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().concat("9"));
            }
        });
        period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().concat("."));
            }
        });
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().concat("0"));
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPrice.setText(currentPrice.getText().toString().substring(0, currentPrice.getText().toString().length() - 1));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_payment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_send) {

            Log.d("FB token", AccessToken.getCurrentAccessToken().getToken());
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("Loading. Please Wait.");
            progress.setCancelable(false);
            progress.show();

            //  api call
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
            Pay payment = new Pay();
            payment.amount = currentPrice.getText().toString();
            payment.message = "Thanks for paying!";
            String key = friends.getText().toString();
            String path = friendsArray.get(key);
            api.postTransaction(path, payment, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.d("/users/transactions", Integer.toString(response.getStatus()));
                    startActivity(new Intent(SendPaymentActivity.this, MainActivity.class));

                    progress.cancel();
                }

                @Override
                public void failure(RetrofitError error) {
                    if (error.getResponse() != null) {
                        Log.e("/users/transactions", Integer.toString(error.getResponse().getStatus()));
                    }
                    startActivity(new Intent(SendPaymentActivity.this, MainActivity.class));
                    progress.cancel();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
