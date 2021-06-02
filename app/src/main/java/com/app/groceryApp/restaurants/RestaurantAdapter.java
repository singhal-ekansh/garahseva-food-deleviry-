package com.app.groceryApp.restaurants;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.groceryApp.R;
import com.app.groceryApp.fragments.FoodFragment;
import com.bumptech.glide.Glide;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    List<RestaurantData> restaurantDataList;
    Context context;
    List<String> ids;
    FoodFragment foodFragment;

    public RestaurantAdapter(List<RestaurantData> restaurantDataList, List<String> ids, Context context, FoodFragment foodFragment) {
        this.restaurantDataList = restaurantDataList;
        this.context = context;
        this.ids = ids;
        this.foodFragment = foodFragment;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_card, parent, false);
        return new RestaurantAdapter.RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.RestaurantViewHolder holder, int position) {
        RestaurantData restaurantData = restaurantDataList.get(position);
        holder.name.setText(restaurantData.getName());
        holder.subText.setText(restaurantData.getCusine());
        Glide.with(context).load(restaurantData.getImage()).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RestaurantDetailActivity.class);
                intent.putExtra("_id", ids.get(position));
                intent.putExtra("res_name", restaurantData.getName());
                foodFragment.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantDataList.size();
    }


    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        TextView name, subText;
        ImageView imageView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.resName);
            subText = itemView.findViewById(R.id.resSubText);
            imageView = itemView.findViewById(R.id.resImage);
        }
    }
}
