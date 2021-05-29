package com.app.groceryApp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetLocation extends BottomSheetDialogFragment {

    Button vik, dak, her;

    public BottomSheetLocation() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }
        return dialog;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        vik = view.findViewById(R.id.BtnVikas);
        dak = view.findViewById(R.id.BtnDak);
        her = view.findViewById(R.id.BtnHer);

        vik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefConfig.setDeliverLocation(getContext(), "Vikasnagar");
                dismiss();
            }
        });

        dak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefConfig.setDeliverLocation(getContext(), "Dakpatthar");
                dismiss();

            }
        });

        her.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefConfig.setDeliverLocation(getContext(), "Herbertpur");
                dismiss();
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }
}
