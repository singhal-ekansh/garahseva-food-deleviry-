package com.app.groceryApp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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

import java.util.ArrayList;

public class cartAdapter extends RecyclerView.Adapter<cartAdapter.cartItemViewHolder> {

    ArrayList<itemHelperClass> cartItemList;
    onCartItemClickListener onCartItemClickListener;
    Context context;

    public cartAdapter(ArrayList<itemHelperClass> cartItemList, onCartItemClickListener onCartItemClickListener, Context context) {
        this.cartItemList = cartItemList;
        this.context = context;
        this.onCartItemClickListener = onCartItemClickListener;
    }

    @NonNull
    @Override
    public cartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_card, parent, false);
        return new cartAdapter.cartItemViewHolder(view, onCartItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull cartItemViewHolder holder, int position) {

        itemHelperClass helperClass = cartItemList.get(position);

        if (helperClass.getDiscount() == 0) {
            holder.discountView.setVisibility(View.GONE);
            holder.cartItemPriceView.setText("MRP: ₹ " + helperClass.getItemPrice());
            holder.cartOriginalPriceView.setVisibility(View.GONE);
        } else {
            holder.discountView.setVisibility(View.VISIBLE);
            holder.cartOriginalPriceView.setVisibility(View.VISIBLE);
            holder.cartOriginalPriceView.setText("MRP: ₹ " + helperClass.getItemPrice());
            holder.cartOriginalPriceView.setPaintFlags(holder.cartOriginalPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discountView.setText(helperClass.getDiscount() + "% \n off");

            double newPrice = helperClass.getItemPrice() - helperClass.getItemPrice() * (helperClass.getDiscount() * 0.01);
            holder.cartItemPriceView.setText("₹ " + newPrice);
        }
        holder.cartItemImageView.setImageResource(context.getResources().getIdentifier(helperClass.getItemImage(), "drawable", context.getPackageName()));
        holder.cartItemNameView.setText(helperClass.getItemName());

        holder.itemCounter.setText("" + helperClass.getItemQuantity());


    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class cartItemViewHolder extends RecyclerView.ViewHolder {

        final ImageView cartItemImageView;
        final TextView cartItemNameView;
        final TextView cartItemPriceView, cartOriginalPriceView, discountView;
        final TextView itemCounter;
        final Button plusBtn, minusBtn;
        final LinearLayout counterView;
        onCartItemClickListener onCartItemClickListener;

        public cartItemViewHolder(@NonNull View itemView, final onCartItemClickListener onCartItemClickListener) {
            super(itemView);

            cartItemImageView = itemView.findViewById(R.id.cartItemImage);
            cartItemNameView = itemView.findViewById(R.id.cartItemName);
            cartItemPriceView = itemView.findViewById(R.id.cartItemPrice);
            itemCounter = itemView.findViewById(R.id.itemCount);
            plusBtn = itemView.findViewById(R.id.plus);
            minusBtn = itemView.findViewById(R.id.minus);
            discountView = itemView.findViewById(R.id.cartDiscountPercent);
            cartOriginalPriceView = itemView.findViewById(R.id.cartOriginalPrice);
            counterView = itemView.findViewById(R.id.cartCounterView);
            this.onCartItemClickListener = onCartItemClickListener;

            plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCartItemClickListener.onPlusBtnClick(getAdapterPosition());
                }
            });

            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCartItemClickListener.onMinusBtnClick(getAdapterPosition());
                }
            });
        }
    }

    public interface onCartItemClickListener {
        void onPlusBtnClick(int position);

        void onMinusBtnClick(int position);
    }
}
