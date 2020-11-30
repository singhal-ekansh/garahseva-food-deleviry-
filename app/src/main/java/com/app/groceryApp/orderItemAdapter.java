package com.app.groceryApp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class orderItemAdapter extends RecyclerView.Adapter<orderItemAdapter.orderItemViewHolder> {

    ArrayList<itemHelperClass> itemList;

    public orderItemAdapter(ArrayList<itemHelperClass> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public orderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_card, parent, false);
        return new orderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull orderItemViewHolder holder, int position) {
        itemHelperClass helperClass = itemList.get(position);

        holder.itemNameView.setText(helperClass.getItemName());
        holder.itemQtyView.setText("x" + helperClass.getItemQuantity());
        double newPrice = helperClass.getItemPrice() - helperClass.getItemPrice() * (helperClass.getDiscount() * 0.01);
        holder.itemPriceView.setText("" + newPrice);


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class orderItemViewHolder extends RecyclerView.ViewHolder {

        TextView itemNameView, itemQtyView, itemPriceView;

        public orderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameView = itemView.findViewById(R.id.orderItemName);
            itemQtyView = itemView.findViewById(R.id.orderItemQty);
            itemPriceView = itemView.findViewById(R.id.orderItemPrice);

        }
    }
}
