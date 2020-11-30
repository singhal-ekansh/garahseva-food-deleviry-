package com.app.groceryApp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.categoryViewHolder> {


    final ArrayList<categoryHelperClass> categoryList;
    onCategoryListener onCategoryListener;

    public categoryAdapter(ArrayList<categoryHelperClass> categoryList, onCategoryListener onCategoryListener) {
        this.categoryList = categoryList;
        this.onCategoryListener = onCategoryListener;
    }

    @NonNull
    @Override
    public categoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catagory_card, parent, false);

        return new categoryViewHolder(view, onCategoryListener);

    }

    @Override
    public void onBindViewHolder(@NonNull categoryViewHolder holder, int position) {

        categoryHelperClass helperClass = categoryList.get(position);

        holder.catImageView.setImageResource(helperClass.getCategoryImage());
        holder.catTextView.setText(helperClass.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class categoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView catImageView;
        final TextView catTextView;
        onCategoryListener onCategoryListener;

        public categoryViewHolder(@NonNull View itemView, onCategoryListener onCategoryListener) {
            super(itemView);
            catImageView = itemView.findViewById(R.id.category_img);
            catTextView = itemView.findViewById(R.id.category_name);
            this.onCategoryListener = onCategoryListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            onCategoryListener.onCategoryClick(getAdapterPosition());
        }
    }

    public interface onCategoryListener {
        void onCategoryClick(int position);
    }
}

