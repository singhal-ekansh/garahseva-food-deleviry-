package com.app.garahseva.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.garahseva.R;
import com.app.garahseva.restaurants.RestaurantData;
import com.app.garahseva.utils.prefConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class currentOrderInfo extends AppCompatActivity {

    OrderDetailClass detailsMap;
    FirebaseFirestore firebaseFirestore;
    RecyclerView itemsRecycler;
    orderItemAdapter adapter;
    TextView aAddress, paymentMode, cartAmt, delAmt, totAmt, orderId, placedOn, restName, ordStatus, callBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order_info);

        Intent intent = getIntent();

        Gson gson = new Gson();
        Type type = new TypeToken<OrderDetailClass>() {
        }.getType();

        detailsMap = gson.fromJson(intent.getStringExtra("map"), type);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("Order Summary");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        firebaseFirestore = FirebaseFirestore.getInstance();
        itemsRecycler = findViewById(R.id.itemsList);
        aAddress = findViewById(R.id.customerAddress);
        paymentMode = findViewById(R.id.payMode);
        cartAmt = findViewById(R.id.cartAmt);
        delAmt = findViewById(R.id.deliveryAmount);
        totAmt = findViewById(R.id.totalAmount);
        orderId = findViewById(R.id.payId);
        placedOn = findViewById(R.id.placedOn);
        restName = findViewById(R.id.restName);
        ordStatus = findViewById(R.id.ordStatus);
        callBtn = findViewById(R.id.callBtn);
        itemsRecycler.setHasFixedSize(true);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        type = new TypeToken<Map<String, Object>>() {
        }.getType();

        Map<String, Object> addressMap = gson.fromJson(detailsMap.getDelivery_address(), type);

        aAddress.setText("Deliver to : " + addressMap.get("delivery address"));
        cartAmt.setText("₹ " + detailsMap.getTotal_amount());
        totAmt.setText("₹ " + (Integer.parseInt(detailsMap.getTotal_amount()) + Integer.parseInt(detailsMap.getDel_fee())));
        delAmt.setText("₹ " + detailsMap.getDel_fee());
        orderId.setText("Id : " + detailsMap.getOrder_id().substring(0, 8).toUpperCase());

        Date date = new Date(Long.parseLong(detailsMap.getTimestamp()) * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  - HH:mm");
        placedOn.setText("Placed on : " + simpleDateFormat.format(date));

        restName.setText("From : " + detailsMap.getRestaurant_name());
        ordStatus.setText("Status : " + detailsMap.getStatus().toUpperCase());

        setItems();


        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBtn.setEnabled(false);
                firebaseFirestore.collection("admin").document(prefConfig.getDeliveryLocation(getApplicationContext())).collection("restaurants").
                        document(detailsMap.getRestaurant_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        callBtn.setEnabled(true);
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + task.getResult().get("contact")));
                            startActivity(intent);

                        }
                    }
                });
            }
        });
    }


    private void setItems() {
        String itemJson = detailsMap.getOrder_detail();
        Gson gson = new Gson();
        Type type = new TypeToken<List<RestaurantData>>() {
        }.getType();

        List<RestaurantData> list;
        list = gson.fromJson(itemJson, type);

        adapter = new orderItemAdapter(list);
        itemsRecycler.setAdapter(adapter);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

}