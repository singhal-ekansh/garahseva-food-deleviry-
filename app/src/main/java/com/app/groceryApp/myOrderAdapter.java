package com.app.groceryApp;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class myOrderAdapter extends RecyclerView.Adapter<myOrderAdapter.ordersViewHolder> {

    List<Map<String, Object>> myOrdersList;
    onOrderClickListener onOrderClickListener;

    public myOrderAdapter(List<Map<String, Object>> myOrdersList, onOrderClickListener onOrderClickListener) {
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

        Map<String, Object> map = myOrdersList.get(position);
        holder.placedDate.setText("Placed On:   " + map.get("placed on").toString());
        holder.orderAmount.setText("Amount:   ₹ " + map.get("total amount"));
        holder.modeOfPayment.setText("Payment Mode:   " + map.get("mode of payment").toString());
        holder.orderId.setText("Order Id:   " + map.get("order id").toString());
        String status = (map.get("status").toString());
        holder.orderStatus.setText(map.get("status").toString());

        if (status.equals("on going"))
            holder.orderStatus.setTextColor(Color.parseColor("#CCA646"));
        else if (status.equals("completed"))
            holder.orderStatus.setTextColor(Color.parseColor("#149414"));
        else
            holder.orderStatus.setTextColor(Color.parseColor("#E00201"));

    }

    @Override
    public int getItemCount() {
        return myOrdersList.size();
    }

    public static class ordersViewHolder extends RecyclerView.ViewHolder {

        TextView placedDate, orderStatus, modeOfPayment, orderAmount, orderId;
        onOrderClickListener onOrderClickListener;

        public ordersViewHolder(@NonNull View itemView, final onOrderClickListener onOrderClickListener) {
            super(itemView);
            placedDate = itemView.findViewById(R.id.placedOnView);
            orderStatus = itemView.findViewById(R.id.orderStatusView);
            modeOfPayment = itemView.findViewById(R.id.paymentType);
            orderAmount = itemView.findViewById(R.id.subAmtView);
            orderId = itemView.findViewById(R.id.orderIdView);
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
