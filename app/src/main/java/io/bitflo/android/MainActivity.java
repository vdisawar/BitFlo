package io.bitflo.android;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.facebook.AccessToken;

import java.util.ArrayList;
import java.util.List;

import io.bitflo.android.adapters.TransactionCardsListAdapter;
import io.bitflo.android.api.BitFloAPI;
import io.bitflo.android.models.CardItem;
import io.bitflo.android.models.Transactions;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<CardItem> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(),
                    R.array.nav_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner = new Spinner(actionBar.getThemedContext());
            spinner.setAdapter(adapter);
            //toolbar.addView(spinner);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                getWindow().setExitTransition(new Explode());
                startActivity(new Intent(MainActivity.this, SendPaymentActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.transaction_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

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
        api.getTransactions(new Callback<Transactions>() {
            @Override
            public void success(Transactions transactions, Response response) {
                Log.d("/users/transactions", Integer.toString(response.getStatus()));

                for (CardItem item : transactions.transactions) {
                    data.add(new CardItem(item.from_facebook, item.from_picture, item.from_name, item.to_facebook, item.to_picture,
                            item.to_name, item.amount, item.message));
                }

                mAdapter = new TransactionCardsListAdapter(data, MainActivity.this.getApplicationContext());
                recyclerView.setAdapter(mAdapter);


            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null) {
                    Log.e("/users/transactions", Integer.toString(error.getResponse().getStatus()));
                }
            }
        });

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

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
                api.getTransactions(new Callback<Transactions>() {
                    @Override
                    public void success(Transactions transactions, Response response) {
                        Log.d("/users/transactions", Integer.toString(response.getStatus()));

                        data.clear();
                        for (CardItem item : transactions.transactions) {
                            data.add(new CardItem(item.from_facebook, item.from_picture, item.from_name, item.to_facebook, item.to_picture,
                                    item.to_name, item.amount, item.message));
                        }

                        mAdapter = new TransactionCardsListAdapter(data, MainActivity.this.getApplicationContext());
                        recyclerView.setAdapter(mAdapter);

                        mSwipeRefreshLayout.setRefreshing(false);


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getResponse() != null) {
                            Log.e("/users/transactions", Integer.toString(error.getResponse().getStatus()));
                        }
                    }
                });

            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
