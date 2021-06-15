package com.app.garahseva.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.app.garahseva.R;

public class Dialogs {

    Context context;
    AlertDialog progressDialog;

    public Dialogs(Context context) {
        this.context = context;
        createLoading();
    }

    public void createLoading() {
        AlertDialog.Builder progressAlert = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        progressAlert.setView(view);
        progressDialog = progressAlert.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

    }

    public void showLoading() {
        progressDialog.show();
    }

    public void cancelLoading() {
        progressDialog.cancel();
    }
}
