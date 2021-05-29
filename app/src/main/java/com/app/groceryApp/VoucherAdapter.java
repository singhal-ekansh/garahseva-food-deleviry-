package com.app.groceryApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

     Context context;
    List<VoucherHelperClass> voucherHelperClassList;

    public VoucherAdapter(Context context,List<VoucherHelperClass> voucherHelperClassList) {
        this.voucherHelperClassList = voucherHelperClassList;
        this.context=context;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vouchers_card, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        VoucherHelperClass helperClass = voucherHelperClassList.get(position);
        holder.codeV.setText(helperClass.getCode());

        String detail =helperClass.getDetail();
        detail=detail.replace("/n","\n");
        holder.detailsV.setText(detail);
    }

    @Override
    public int getItemCount() {
        return voucherHelperClassList.size();
    }

    public  class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView codeV, detailsV;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            codeV = itemView.findViewById(R.id.voucherCode);
            detailsV = itemView.findViewById(R.id.CodeDetailsView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"Apply Voucher at checkout",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
