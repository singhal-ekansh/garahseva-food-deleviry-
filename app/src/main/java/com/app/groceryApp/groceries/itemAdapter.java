package com.app.groceryApp.groceries;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.groceryApp.R;

import java.util.ArrayList;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.itemViewHolder> {

    ArrayList<itemHelperClass> itemList;
    onItemListener onItemListener;
    Context context;

    public itemAdapter(ArrayList<itemHelperClass> itemList, onItemListener onItemListener, Context context) {
        this.itemList = itemList;
        this.context = context;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new itemViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

        itemHelperClass helperClass = itemList.get(position);


        holder.itemImageView.setImageResource(context.getResources().getIdentifier(helperClass.getItemImage(), "drawable", context.getPackageName()));


        holder.itemNameView.setText(helperClass.getItemName());
        if (helperClass.getDiscount() == 0) {
            holder.discountPercentView.setVisibility(View.GONE);
            holder.itemPriceView.setText("MRP: ₹ " + helperClass.getItemPrice());
            holder.originalPriceView.setVisibility(View.GONE);
        } else {
            holder.discountPercentView.setVisibility(View.VISIBLE);
            holder.originalPriceView.setVisibility(View.VISIBLE);
            holder.originalPriceView.setText("MRP: ₹ " + helperClass.getItemPrice());
            holder.originalPriceView.setPaintFlags(holder.originalPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discountPercentView.setText(helperClass.getDiscount() + "% \n off");
            double newPrice = helperClass.getItemPrice() - helperClass.getItemPrice() * (helperClass.getDiscount() * 0.01);
            holder.itemPriceView.setText("₹ " + newPrice);
        }


        if (helperClass.getItemQuantity() == 0) {
            holder.addBtn.setVisibility(View.VISIBLE);
            holder.counterLayout.setVisibility(View.GONE);
        } else {
            holder.addBtn.setVisibility(View.GONE);
            holder.counterLayout.setVisibility(View.VISIBLE);
            holder.counter.setText("" + helperClass.getItemQuantity());

        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class itemViewHolder extends RecyclerView.ViewHolder {

        final ImageView itemImageView;
        final TextView itemNameView;
        final TextView itemPriceView;
        final TextView counter, originalPriceView, discountPercentView;
        final Button addBtn, increase, decrease;
        final LinearLayout counterLayout;
        onItemListener onItemListener;

        public itemViewHolder(@NonNull View itemView, final onItemListener onItemListener) {
            super(itemView);
            this.onItemListener = onItemListener;
            itemImageView = itemView.findViewById(R.id.itemImage);
            itemNameView = itemView.findViewById(R.id.itemName);
            itemPriceView = itemView.findViewById(R.id.itemPrice);
            addBtn = itemView.findViewById(R.id.addButton);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            counter = itemView.findViewById(R.id.itemCount);
            discountPercentView = itemView.findViewById(R.id.discountPercent);
            originalPriceView = itemView.findViewById(R.id.originalPrice);

            counterLayout = itemView.findViewById(R.id.counterLayout);

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemListener.onAddBtnClick(getAdapterPosition());
                    addBtn.setVisibility(View.GONE);
                    counterLayout.setVisibility(View.VISIBLE);

                }
            });
            increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String count = counter.getText().toString();
                    int countValue = Integer.parseInt(count);
                    countValue++;
                    counter.setText(String.valueOf(countValue));
                    onItemListener.onIncreaseBtnClick(getAdapterPosition(), countValue);


                }
            });

            decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String count = counter.getText().toString();
                    int countValue = Integer.parseInt(count);
                    countValue--;
                    if (countValue == 0) {


                        addBtn.setVisibility(View.VISIBLE);
                        counterLayout.setVisibility(View.GONE);
                    } else {

                        counter.setText(String.valueOf(countValue));
                    }
                    onItemListener.onDecreaseBtnClick(getAdapterPosition(), countValue);
                }
            });

        }


    }

    public interface onItemListener {
        void onAddBtnClick(int position);

        void onIncreaseBtnClick(int position, int value);

        void onDecreaseBtnClick(int position, int value);
    }


}
