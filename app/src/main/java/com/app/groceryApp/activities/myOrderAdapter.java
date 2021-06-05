package com.app.groceryApp.activities;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.groceryApp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class myOrderAdapter extends RecyclerView.Adapter<myOrderAdapter.ordersViewHolder> {

    List<OrderDetailClass> myOrdersList;
    onOrderClickListener onOrderClickListener;

    public myOrderAdapter(List<OrderDetailClass> myOrdersList, onOrderClickListener onOrderClickListener) {
        this.myOrdersList = myOrdersList;
        this.onOrderClickListener = onOrderClickListener;
    }

    @NonNull
    @Override
    public ordersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_card, parent, false);
        return new myOrderAdapter.ordersViewHolder(view, onOrderClickListener);
    }


    @Override
    public void onBindViewHolder(@NonNull ordersViewHolder holder, int position) {

        OrderDetailClass orderDetail = myOrdersList.get(position);
        Date date = new Date(Long.parseLong(orderDetail.getTimestamp()) * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  - HH:mm");
        holder.placedDate.setText("Placed on : " + simpleDateFormat.format(date));
        holder.orderAmount.setText("Amount : ₹ " + (Integer.parseInt(orderDetail.getTotal_amount()) + Integer.parseInt(orderDetail.getDel_fee())));
        holder.modeOfPayment.setText("Payment Mode : Cash on delivery");
        holder.orderId.setText("Id : " + orderDetail.getOrder_id().substring(0, 8).toUpperCase());
        holder.restaurantName.setText(orderDetail.getRestaurant_name());
        String status = orderDetail.getStatus();
        holder.orderStatus.setText(status);

        if (status.equals("pending"))
            holder.orderStatus.setTextColor(Color.parseColor("#CCA646"));
        else if (status.equals("delivered"))
            holder.orderStatus.setTextColor(Color.parseColor("#149414"));
        else
            holder.orderStatus.setTextColor(Color.parseColor("#E00201"));

    }

    @Override
    public int getItemCount() {
        return myOrdersList.size();
    }

    public static class ordersViewHolder extends RecyclerView.ViewHolder {

        TextView placedDate, orderStatus, modeOfPayment, orderAmount, orderId, restaurantName;
        onOrderClickListener onOrderClickListener;

        public ordersViewHolder(@NonNull View itemView, final onOrderClickListener onOrderClickListener) {
            super(itemView);
            placedDate = itemView.findViewById(R.id.placedOnView);
            orderStatus = itemView.findViewById(R.id.orderStatusView);
            modeOfPayment = itemView.findViewById(R.id.paymentType);
            orderAmount = itemView.findViewById(R.id.subAmtView);
            orderId = itemView.findViewById(R.id.orderIdView);
            restaurantName = itemView.findViewById(R.id.orderResName);
            this.onOrderClickListener = onOrderClickListener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOrderClickListener.onOrderClick(getAdapterPosition());
                }
            });

        }
    }

    public interface onOrderClickListener {
        void onOrderClick(int position);

    }
}
