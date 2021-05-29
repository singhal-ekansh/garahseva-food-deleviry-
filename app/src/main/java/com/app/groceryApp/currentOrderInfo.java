package com.app.groceryApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class currentOrderInfo extends AppCompatActivity {

    Map<String, Object> detailsMap;

    Button cancelOrderButton;
    FirebaseFirestore firebaseFirestore;
    String userOrderNo, uid, place;
    int userOrderNumberInInt, oId;
    RecyclerView itemsRecycler;
    RecyclerView.Adapter adapter;
    TextView aName, aNumber, aAddress, aLandmark, aPostal, paymentMode, cartAmt, delAmt, totAmt, orderId, walletCashUsed, walletView;
    AlertDialog progressDialog, cancelDial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order_info);

        Intent intent = getIntent();


        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();

        detailsMap = gson.fromJson(intent.getStringExtra("map"), type);

        ActionBar bar = getSupportActionBar();

        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        cancelOrderButton = findViewById(R.id.cancelOrderBtn);
        firebaseFirestore = FirebaseFirestore.getInstance();
        itemsRecycler = findViewById(R.id.itemsList);
        aName = findViewById(R.id.customerName);
        aNumber = findViewById(R.id.customerNumber);
        aAddress = findViewById(R.id.customerAddress);
        aLandmark = findViewById(R.id.customerLandmark);
        aPostal = findViewById(R.id.customerPostal);
        paymentMode = findViewById(R.id.payMode);
        cartAmt = findViewById(R.id.cartAmt);
        delAmt = findViewById(R.id.deliveryAmount);
        totAmt = findViewById(R.id.totalAmount);
        orderId = findViewById(R.id.payId);
        walletCashUsed = findViewById(R.id.walletAmount);
        walletView = findViewById(R.id.l4);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setItems();
        setAddress();
        setPaymentDetails();

        if (detailsMap.get("status").toString().equals("on going")) {
            cancelOrderButton.setVisibility(View.VISIBLE);
        }

        cancelOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });

    }

    private void setPaymentDetails() {
        paymentMode.setText(detailsMap.get("mode of payment").toString());


        int totalAmount = (int) (double) detailsMap.get("total amount");
        int deliveryFee = (int) (double) detailsMap.get("delivery fee");
        int walletCash = (int) (double) detailsMap.get("wallet cash");
        int Id = (int) (double) detailsMap.get("order id");

        if (walletCash != 0) {
            walletView.setVisibility(View.VISIBLE);
            walletCashUsed.setVisibility(View.VISIBLE);
            walletCashUsed.setText("- ₹ " + walletCash);
        }

        delAmt.setText("₹ " + deliveryFee);
        totAmt.setText("₹ " + (totalAmount + deliveryFee - walletCash));
        cartAmt.setText("₹ " + (totalAmount));

        orderId.setText("" + Id);

    }

    private void setAddress() {
        String addressJson = detailsMap.get("delivery address").toString();
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();

        Map<String, Object> addressMap = gson.fromJson(addressJson, type);

        aName.setText("Name:  " + addressMap.get("name").toString());
        aNumber.setText("Contact:  " + addressMap.get("contact").toString());
        aAddress.setText("Address:  " + addressMap.get("delivery address").toString());
        aLandmark.setText("Landmark:  " + addressMap.get("landmark").toString());
        aPostal.setText("Postal Code:  " + addressMap.get("postal code").toString());

        String postal = addressMap.get("postal code").toString();
        switch (postal) {
            case "248198":
                place = "Vikasnagar";
                break;

            case "248125":
                place = "Dakpatthar";
                break;

            case "248142":
                place = "Herbertpur";
                break;


        }

    }

    private void setItems() {
        itemsRecycler.setHasFixedSize(true);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        String itemJson = detailsMap.get("order detail").toString();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<itemHelperClass>>() {
        }.getType();

        ArrayList<itemHelperClass> list;
        list = gson.fromJson(itemJson, type);

        adapter = new orderItemAdapter(list);
        itemsRecycler.setAdapter(adapter);

    }

    private void ProgressDialog() {
        AlertDialog.Builder progressAlert = new AlertDialog.Builder(currentOrderInfo.this);
        View view = getLayoutInflater().inflate(R.layout.progress_dialog, null);

        progressAlert.setView(view);
        progressDialog = progressAlert.create();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        progressDialog.show();
        cancelDial.dismiss();

    }

    private void confirmDialog() {
        AlertDialog.Builder confirmAlert = new AlertDialog.Builder(currentOrderInfo.this);
        View view = getLayoutInflater().inflate(R.layout.cancel_dialog, null);

        Button yes, no;
        yes = view.findViewById(R.id.yesBtn);
        no = view.findViewById(R.id.noBtn);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDial.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog();


                firebaseFirestore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (!document.getBoolean("has current order")) {
                                finish();
                                return;

                            }

                            userOrderNumberInInt = (int) (long) document.get("no of orders");
                            if (userOrderNumberInInt < 10)
                                userOrderNo = "0" + userOrderNumberInInt;
                            else
                                userOrderNo = Integer.toString(userOrderNumberInInt);


                            oId = (int) (double) detailsMap.get("order id");
                            detailsMap.put("order id", oId);
                            detailsMap.put("status", "cancelled");

                            firebaseFirestore.collection("users").document(uid).collection("orders").document(userOrderNo).update("status", "cancelled");

                            firebaseFirestore.collection("users").document(uid).update("has current order", false);


                            firebaseFirestore.collection("admin").document(place).collection("current orders").document(String.valueOf((int) detailsMap.get("order id"))).delete();

                            firebaseFirestore.collection("admin").document(place).collection("completed orders").document(String.valueOf((int) detailsMap.get("order id"))).set(detailsMap);

                            prefConfig.removeHasCurrentOrder(getApplicationContext());
                            progressDialog.dismiss();
                            finish();
                            overridePendingTransition(0, 0);

                        }
                    }
                });

            }
        });

        confirmAlert.setView(view);
        cancelDial = confirmAlert.create();
        cancelDial.setCanceledOnTouchOutside(false);
        cancelDial.setCancelable(false);

        cancelDial.show();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}