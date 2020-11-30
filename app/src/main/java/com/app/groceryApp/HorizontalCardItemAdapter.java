package com.app.groceryApp;

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

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class HorizontalCardItemAdapter extends RecyclerView.Adapter<HorizontalCardItemAdapter.horizontalItemViewHolder> {
    ArrayList<itemHelperClass> hItemList;
    onHorizontalItemListener onHorizontalItemListener;
    Context context;

    public HorizontalCardItemAdapter(ArrayList<itemHelperClass> hItemList, HorizontalCardItemAdapter.onHorizontalItemListener onHorizontalItemListener, Context context) {
        this.hItemList = hItemList;
        this.onHorizontalItemListener = onHorizontalItemListener;
        this.context = context;
    }


    @NonNull
    @Override
    public horizontalItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_card, parent, false);
        return new HorizontalCardItemAdapter.horizontalItemViewHolder(view, onHorizontalItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull horizontalItemViewHolder holder, int position) {
        itemHelperClass helperClass = hItemList.get(position);

        holder.hItemImageView.setImageResource(context.getResources().getIdentifier(helperClass.getItemImage(), "drawable", context.getPackageName()));

        holder.hItemNameView.setText(helperClass.getItemName());
        if (helperClass.getDiscount() == 0) {
            holder.hDiscountPercentView.setVisibility(View.GONE);
            holder.hItemPriceView.setText("MRP: ₹ " + helperClass.getItemPrice());
            holder.hOriginalPriceView.setVisibility(View.INVISIBLE);
        } else {
            holder.hDiscountPercentView.setVisibility(View.VISIBLE);
            holder.hOriginalPriceView.setVisibility(View.VISIBLE);
            holder.hOriginalPriceView.setText("MRP: ₹ " + helperClass.getItemPrice());
            holder.hOriginalPriceView.setPaintFlags(holder.hOriginalPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.hDiscountPercentView.setText(helperClass.getDiscount() + "% \n off");
            double newPrice = helperClass.getItemPrice() - helperClass.getItemPrice() * (helperClass.getDiscount() * 0.01);
            holder.hItemPriceView.setText("₹ " + newPrice);
        }


        if (helperClass.getItemQuantity() == 0) {
            holder.hAddBtn.setVisibility(View.VISIBLE);
            holder.hCounterLayout.setVisibility(View.GONE);
        } else {
            holder.hAddBtn.setVisibility(View.GONE);
            holder.hCounterLayout.setVisibility(View.VISIBLE);
            holder.hCounter.setText("" + helperClass.getItemQuantity());

        }
    }

    @Override
    public int getItemCount() {
        return hItemList.size();
    }

    public static class horizontalItemViewHolder extends RecyclerView.ViewHolder {

        final ImageView hItemImageView;
        final TextView hItemNameView;
        final TextView hItemPriceView;
        final TextView hCounter, hOriginalPriceView, hDiscountPercentView;
        final Button  hAddBtn,hIncrease, hDecrease;
        final LinearLayout hCounterLayout;
        onHorizontalItemListener onHorizontalItemListener;

        public horizontalItemViewHolder(@NonNull View itemView, final HorizontalCardItemAdapter.onHorizontalItemListener onHorizontalItemListener) {
            super(itemView);
            this.onHorizontalItemListener = onHorizontalItemListener;
            hItemImageView = itemView.findViewById(R.id.HorizontalCardImage);
            hItemNameView = itemView.findViewById(R.id.HorizontalCardName);
            hItemPriceView = itemView.findViewById(R.id.HorizontalCardItemPrice);
            hAddBtn = itemView.findViewById(R.id.HorizontalCardAddBtn);
            hIncrease = itemView.findViewById(R.id.horizontalIncrease);
            hDecrease = itemView.findViewById(R.id.horizontalDecrease);
            hCounter = itemView.findViewById(R.id.horizontalItemCount);
            hDiscountPercentView = itemView.findViewById(R.id.HorizontalCardDiscount);
            hOriginalPriceView = itemView.findViewById(R.id.HorizontalCardOriginalPrice);

            hCounterLayout = itemView.findViewById(R.id.HorizontalCardCounterLayout);

            hAddBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onHorizontalItemListener.onAddBtnClick(getAdapterPosition());
                    hAddBtn.setVisibility(View.GONE);
                    hCounterLayout.setVisibility(View.VISIBLE);

                }
            });
            hIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String count = hCounter.getText().toString();
                    int countValue = Integer.parseInt(count);
                    countValue++;
                    hCounter.setText(String.valueOf(countValue));
                    onHorizontalItemListener.onIncreaseBtnClick(getAdapterPosition(), countValue);


                }
            });

            hDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String count = hCounter.getText().toString();
                    int countValue = Integer.parseInt(count);
                    countValue--;
                    if (countValue == 0) {


                        hAddBtn.setVisibility(View.VISIBLE);
                        hCounterLayout.setVisibility(View.GONE);
                    } else {

                        hCounter.setText(String.valueOf(countValue));
                    }
                    onHorizontalItemListener.onDecreaseBtnClick(getAdapterPosition(), countValue);
                }
            });

        }


    }

    public interface onHorizontalItemListener {
        void onAddBtnClick(int position);

        void onIncreaseBtnClick(int position, int value);

        void onDecreaseBtnClick(int position, int value);
    }

}



