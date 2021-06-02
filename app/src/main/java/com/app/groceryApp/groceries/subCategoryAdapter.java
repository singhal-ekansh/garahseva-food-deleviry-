package com.app.groceryApp.groceries;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.groceryApp.R;

import java.util.ArrayList;

public class subCategoryAdapter extends RecyclerView.Adapter<subCategoryAdapter.subCategoryViewHolder> {

    final ArrayList<categoryHelperClass> subCategoryList;
    subCategoryAdapter.onSubCategoryListener onSubCategoryListener;

    public subCategoryAdapter(ArrayList<categoryHelperClass> subCategoryList, onSubCategoryListener onSubCategoryListener) {
        this.subCategoryList = subCategoryList;
        this.onSubCategoryListener = onSubCategoryListener;
    }

    @NonNull
    @Override
    public subCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_catagory_card, parent, false);

        return new subCategoryAdapter.subCategoryViewHolder(view, onSubCategoryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull subCategoryViewHolder holder, int position) {
        categoryHelperClass helperClass = subCategoryList.get(position);

        holder.catImageView.setImageResource(helperClass.getCategoryImage());
        holder.catTextView.setText(helperClass.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return subCategoryList.size();
    }

    public static class subCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView catImageView;
        final TextView catTextView;
        onSubCategoryListener onSubCategoryListener;

        public subCategoryViewHolder(@NonNull View itemView, subCategoryAdapter.onSubCategoryListener onSubCategoryListener) {
            super(itemView);
            catImageView = itemView.findViewById(R.id.subCategory_img);
            catTextView = itemView.findViewById(R.id.subCategory_name);
            this.onSubCategoryListener = onSubCategoryListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            onSubCategoryListener.onSubCategoryClick(getAdapterPosition());
        }
    }

    public interface onSubCategoryListener {
        void onSubCategoryClick(int position);
    }
}
