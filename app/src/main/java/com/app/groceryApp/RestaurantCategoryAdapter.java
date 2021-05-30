package com.app.groceryApp;

import android.content.Context;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestaurantCategoryAdapter extends RecyclerView.Adapter<RestaurantCategoryAdapter.RestaurantCategoryViewHolder> {

    Map<String, List<RestaurantData>> categoryItemMap;
    Context context;
    List<String> categories;

    public RestaurantCategoryAdapter(Map<String, List<RestaurantData>> categoryItemMap, Context context) {
        this.categoryItemMap = categoryItemMap;
        this.context = context;
        this.categories = new ArrayList<>();
        categories.addAll(categoryItemMap.keySet());
    }

    @NonNull
    @Override
    public RestaurantCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.res_category_expand_card, parent, false);
        return new RestaurantCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantCategoryAdapter.RestaurantCategoryViewHolder holder, int position) {

        holder.catName.setText(categories.get(position));
        holder.itemsRecycler.setLayoutManager(new LinearLayoutManager(context));
        holder.itemsRecycler.setHasFixedSize(true);
        RestaurantItemsAdapter adapter = new RestaurantItemsAdapter(categoryItemMap.get(categories.get(position)), context);
        holder.itemsRecycler.setAdapter(adapter);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.itemsRecycler.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(holder.catCard);
                    holder.itemsRecycler.setVisibility(View.VISIBLE);
                    holder.arrowSign.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                } else {
                    holder.itemsRecycler.setVisibility(View.GONE);
                    holder.arrowSign.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryItemMap.size();
    }

    public static class RestaurantCategoryViewHolder extends RecyclerView.ViewHolder {

        TextView catName;
        ImageView arrowSign;
        RecyclerView itemsRecycler;
        ConstraintLayout catCard;

        public RestaurantCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            catName = itemView.findViewById(R.id.resCatName);
            arrowSign = itemView.findViewById(R.id.catArrow);
            itemsRecycler = itemView.findViewById(R.id.resCatRecycler);
            catCard = itemView.findViewById(R.id.catCard);

        }
    }

}
