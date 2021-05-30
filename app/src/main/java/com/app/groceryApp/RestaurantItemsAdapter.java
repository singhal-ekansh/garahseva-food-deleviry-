package com.app.groceryApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import java.util.List;

public class RestaurantItemsAdapter extends RecyclerView.Adapter<RestaurantItemsAdapter.RestaurantItemsViewHolder> {

    List<RestaurantData> restaurantData;
    Context context;

    public RestaurantItemsAdapter(List<RestaurantData> restaurantData, Context context) {
        this.restaurantData = restaurantData;
        this.context = context;
    }

    @NonNull
    @Override
    public RestaurantItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_items_card, parent, false);
        return new RestaurantItemsAdapter.RestaurantItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantItemsAdapter.RestaurantItemsViewHolder holder, int position) {
        RestaurantData data = restaurantData.get(position);
        holder.name.setText(data.getName());
        holder.price.setText("₹ " + data.getPrice());

        if(prefConfig.getCurrentRestaurant(context).equals(((RestaurantDetailActivity) context)._id)){
            List<RestaurantData> list = prefConfig.getFoodOrderList(context);
            for (RestaurantData resData : list) {
                if (resData.getName().equals(data.getName())) {
                    holder.addBtn.setVisibility(View.GONE);
                    holder.elegantNumberButton.setVisibility(View.VISIBLE);
                    holder.elegantNumberButton.setNumber(resData.getQuantitySelected());
                    break;
                }
            }

        }

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (prefConfig.getCurrentRestaurant(context).equals(""))
                    prefConfig.changeCurrentRestaurant(context, ((RestaurantDetailActivity) context)._id);
                else if (!prefConfig.getCurrentRestaurant(context).equals(((RestaurantDetailActivity) context)._id)) {
                    //TODO: dialog to discard
                    return;
                }

                holder.addBtn.setVisibility(View.GONE);
                holder.elegantNumberButton.setVisibility(View.VISIBLE);
                holder.elegantNumberButton.setNumber("1");

                List<RestaurantData> list = prefConfig.getFoodOrderList(context);
                list.add(new RestaurantData(data.getName(), data.getPrice(), "1"));
                prefConfig.saveFoodOrderList(context, list);
            }
        });
        holder.elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                List<RestaurantData> list = prefConfig.getFoodOrderList(context);
                int i = 0;
                for (RestaurantData resData : list) {
                    if (resData.getName().equals(data.getName())) {
                        break;
                    }
                    i++;
                }

                if (newValue == 0) {
                    holder.addBtn.setVisibility(View.VISIBLE);
                    holder.elegantNumberButton.setVisibility(View.GONE);
                    list.remove(i);
                } else {
                    list.get(i).setQuantitySelected(String.valueOf(newValue));
                }
                prefConfig.saveFoodOrderList(context, list);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantData.size();
    }

    public static class RestaurantItemsViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, addBtn;
        ElegantNumberButton elegantNumberButton;

        public RestaurantItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.itemPrice);
            addBtn = itemView.findViewById(R.id.addBtn);
            elegantNumberButton = itemView.findViewById(R.id.elegantNumberButton);

        }
    }
}
