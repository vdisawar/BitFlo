package io.bitflo.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import io.bitflo.android.api.BitFloAPI;
import io.bitflo.android.models.Balance;
import io.bitflo.android.models.EmptyObject;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView imageView = (ImageView) findViewById(R.id.profile_image);
        TextView tvName = (TextView) findViewById(R.id.profile_name);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String name = prefs.getString(Constants.PREFS_FB_NAME, "");
        String fbUrl = prefs.getString(Constants.PREFS_FB_PIC, null);
        tvName.setText(name);

        if (fbUrl != null && !fbUrl.isEmpty())
            Picasso.with(this).load(fbUrl).transform(new CircleTransform()).into(imageView);
//
//        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//        Bitmap newBitmap = getRoundedCornerBitmap(bitmap, 100);
//        imageView.setImageBitmap(newBitmap);

        String brand = prefs.getString(Constants.CARD_BRAND, "");
        String cardNum = prefs.getString(Constants.CARD_NUMBER, "");

        TextView brandTV = (TextView) findViewById(R.id.card_brand);
        TextView numTV = (TextView) findViewById(R.id.card_number_display);
        brandTV.setText(brand);
        numTV.setText(cardNum);

        final TextView balanceTV = (TextView) findViewById(R.id.bitflo_balance);

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
        api.getBalance(new Callback<Balance>() {
            @Override
            public void success(Balance balance, Response response) {
                Log.d("/users/balance", Integer.toString(response.getStatus()));

                balanceTV.setText(new StringBuilder().append("Current BitFlo Balance \n $ ").append(balance.balance));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null) {
                    Log.e("/users/balance", Integer.toString(error.getResponse().getStatus()));
                }
            }
        });

        Button withdraw = (Button) findViewById(R.id.btn_withdraw);
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EmptyObject obj = new EmptyObject();
                obj.empty = "";
                api.withdraw(obj, new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Log.d("/users/withdraw", Integer.toString(response.getStatus()));

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getResponse() != null) {
                            Log.e("/users/withdraw", Integer.toString(error.getResponse().getStatus()));
                        }
                    }
                });

            }
        });
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = Color.parseColor("#3F51B5");
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            startActivity(new Intent(this, AddPaymentActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class CircleTransform implements Transformation {

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size/2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
