package com.hinddev.pdf.o37i.instantapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.hinddev.pdf.o37i.instantapp.R;
import com.hinddev.pdf.o37i.instantapp.listener.OnOrderReadyListener;
import com.hinddev.pdf.o37i.instantapp.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders = new ArrayList<>();
    private Context context;
    CountDownTimer countDownTimer;

    OnOrderReadyListener onOrderReadyListener ;

    public OrderAdapter(Context context , OnOrderReadyListener onOrderReadyListener) {
        this.context = context;
        this.onOrderReadyListener = onOrderReadyListener;



    }

    public void onOrderAdded(DocumentChange change) {

        orders.add(change.getDocument().toObject(Order.class));
//            notifyItemInserted(change.getNewIndex());
        notifyItemInserted(orders.size() - 1);


    }

    public void onDocumentModified(DocumentChange change) {
//
//        if (countDownTimer!=null) {
//            countDownTimer.cancel();
//        }

        if (change.getOldIndex() == change.getNewIndex()) {

            // Item changed but remained in same position

            orders.remove(change.getOldIndex());
            orders.add(change.getOldIndex(), change.getDocument().toObject(Order.class));
            // orders.set(change.getOldIndex(), change.getDocument().toObject(Order.class));
            notifyItemChanged(change.getOldIndex());
//            Log.d("TAG", "onDocumentModified: " + change.getDocument().toObject(Order.class).getTime());


        } else {
            // Item changed and changed position
            orders.remove(change.getOldIndex());
            orders.add(change.getNewIndex(), change.getDocument().toObject(Order.class));
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }

    }

    public void onDocumentRemoved(DocumentChange change) {
        orders.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);

        return new OrderAdapter.OrderViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        Order order = orders.get(position);
        if (order.isStats()) {
            onOrderReadyListener.onOrderReady(order);
            if (countDownTimer!=null) {
//                countDownTimer.cancel();
            }
            holder.orderStats.setText("Ready");
            holder.orderStats.setTextColor(context.getColor(R.color.green));
            holder.barView.setBackground(context.getDrawable(R.drawable.green_left_bar_background));

        }else {

            long time = order.getTime() * 60L * 1000L;

            holder.orderStats.setText("");

            if (holder.timer != null) {
                holder.timer.cancel();
            }
            holder.timer = new CountDownTimer(time, 1000) {

                @Override
                public void onTick(long l) {
                    long second = (l / 1000) % 60;
                    long minutes = (l / (1000 * 60)) % 60;
                    holder.orderStats.setText(minutes + ":" + second);
                    holder.orderStats.setTextColor(context.getColor(R.color.red));
                    holder.barView.setBackground(context.getDrawable(R.drawable.red_left_bar_background));
                }

                @Override
                public void onFinish() {
                    holder.orderStats.setText("Waiting");
                    holder.orderStats.setTextColor(context.getColor(R.color.orange));
                    holder.barView.setBackground(context.getDrawable(R.drawable.orange_left_bar_background));

                }
            }.start();

//            countDownTimer = new CountDownTimer(time, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    long second = (millisUntilFinished / 1000) % 60;
//                    long minutes = (millisUntilFinished / (1000 * 60)) % 60;
//                    holder.orderStats.setText(minutes + ":" + second);
//                    holder.orderStats.setTextColor(context.getColor(R.color.red));
//                    holder.barView.setBackground(context.getDrawable(R.drawable.red_left_bar_background));
//
//                }
//
//                @Override
//                public void onFinish() {
//                    holder.orderStats.setText("Waiting");
//                    holder.orderStats.setTextColor(context.getColor(R.color.orange));
//                    holder.barView.setBackground(context.getDrawable(R.drawable.orange_left_bar_background));
//
//                }
//            }.start();
        }

        holder.orderName.setText(orders.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView orderName;
        public TextView orderStats;
        public View barView;
        CountDownTimer timer;


        public OrderViewHolder(View view) {
            super(view);

            orderName = view.findViewById(R.id.txtOrderName);
            orderStats = view.findViewById(R.id.txtOrderStats);
            barView = view.findViewById(R.id.vieww);
        }
    }

}
