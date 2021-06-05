package com.app.groceryApp.restaurants;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.groceryApp.R;
import com.app.groceryApp.fragments.FoodFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

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

        if (restaurantData.getIsClosed()) {
            holder.closedTag.setVisibility(View.VISIBLE);
            holder.resCard.setCardBackgroundColor(Color.parseColor("#A9A9A9"));
            holder.imageView.setAlpha((float) 0.5);
        }else{
            holder.closedTag.setVisibility(View.GONE);
            holder.resCard.setCardBackgroundColor(Color.parseColor("#ffffff"));
            holder.imageView.setAlpha((float) 1);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(restaurantData.getIsClosed()){
                    Toast.makeText(context,"Restaurant is closed",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(context, RestaurantDetailActivity.class);
                intent.putExtra("_id", ids.get(position));
                intent.putExtra("res_name", restaurantData.getName());
                intent.putExtra("del_fee", restaurantData.getDeliveryFee());
                intent.putExtra("free_del", restaurantData.getFreeDeliveryPrice());
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
        MaterialCardView closedTag, resCard;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.resName);
            subText = itemView.findViewById(R.id.resSubText);
            imageView = itemView.findViewById(R.id.resImage);
            closedTag = itemView.findViewById(R.id.closedTag);
            resCard = itemView.findViewById(R.id.resCard);
        }
    }
}
