package com.app.groceryApp.restaurants;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.groceryApp.R;
import com.app.groceryApp.activities.PlaceOrderActivity;
import com.app.groceryApp.utils.prefConfig;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import java.util.List;

public class RestaurantItemsAdapter extends RecyclerView.Adapter<RestaurantItemsAdapter.RestaurantItemsViewHolder> {

    List<RestaurantData> restaurantData;
    Context context;
    boolean bypass;

    public RestaurantItemsAdapter(List<RestaurantData> restaurantData, Context context, boolean bypass) {
        this.restaurantData = restaurantData;
        this.context = context;
        this.bypass = bypass;
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


        if (bypass || prefConfig.getCurrentRestaurant(context).equals(((RestaurantDetailActivity) context)._id)) {
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

                if (bypass)
                    return;

                if (prefConfig.getCurrentRestaurant(context).equals(""))
                    prefConfig.changeCurrentRestaurant(context, ((RestaurantDetailActivity) context)._id);
                else if (!prefConfig.getCurrentRestaurant(context).equals(((RestaurantDetailActivity) context)._id)) {
                    prefConfig.clearFoodOrderList(context);
                    prefConfig.changeCurrentRestaurant(context, ((RestaurantDetailActivity) context)._id);
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
                    list.remove(i);
                    holder.addBtn.setVisibility(View.VISIBLE);
                    holder.elegantNumberButton.setVisibility(View.GONE);

                } else {
                    list.get(i).setQuantitySelected(String.valueOf(newValue));
                }
                prefConfig.saveFoodOrderList(context, list);

                if (newValue == 0 && bypass) {
                    if (prefConfig.getFoodOrderList(context).size() == 0) {
                        ((PlaceOrderActivity) context).setResult(Activity.RESULT_OK);
                        ((PlaceOrderActivity) context).finish();
                    } else {
                        restaurantData.clear();
                        restaurantData.addAll(prefConfig.getFoodOrderList(context));
                        notifyDataSetChanged();
                    }
                }
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
