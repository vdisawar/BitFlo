package io.bitflo.android.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import io.bitflo.android.R;
import io.bitflo.android.models.CardItem;

/**
 *
 * Created by Vishal on 10/25/15.
 */
public class TransactionCardsListAdapter extends RecyclerView.Adapter<TransactionCardsListAdapter.ViewHolder> {

    private List<CardItem> transactions;
    private Context mContext;

    public TransactionCardsListAdapter(List<CardItem> transactions, Context context) {
        this.transactions = transactions;
        mContext = context;
    }

    @Override
    public TransactionCardsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TransactionCardsListAdapter.ViewHolder holder, int position) {
        CardItem item = transactions.get(position);
        // load information
        holder.title.setText(new StringBuilder().append(item.to_name).append(" Payed ").append(item.from_name));
        holder.amount.setText(new StringBuilder().append("$ ").append(item.amount));
        holder.message.setText(item.message);
        Picasso.with(mContext).load(item.to_picture).transform(new CircleTransform()).into(holder.picture);
        Picasso.with(mContext).load(item.from_picture).transform(new CircleTransform()).into(holder.picture1);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView amount;
        public TextView message;
        public ImageView picture;
        public ImageView picture1;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.payment_details);
            amount = (TextView) itemView.findViewById(R.id.transaction_amount);
            message = (TextView) itemView.findViewById(R.id.payment_description);
            picture = (ImageView) itemView.findViewById(R.id.payment_picture);
            picture1 = (ImageView) itemView.findViewById(R.id.payment_picture1);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
