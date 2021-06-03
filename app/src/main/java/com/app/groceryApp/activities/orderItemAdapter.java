package com.app.groceryApp.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.groceryApp.R;
import com.app.groceryApp.groceries.itemHelperClass;
import com.app.groceryApp.restaurants.RestaurantData;

import java.util.ArrayList;
import java.util.List;

public class orderItemAdapter extends RecyclerView.Adapter<orderItemAdapter.orderItemViewHolder> {

    List<RestaurantData> itemList;

    public orderItemAdapter(List<RestaurantData> itemList) {
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
        RestaurantData helperClass = itemList.get(position);

        holder.itemNameView.setText(helperClass.getName());
        holder.itemQtyView.setText(helperClass.getQuantitySelected() + " x");
        holder.itemPriceView.setText("₹ " + helperClass.getPrice());


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
