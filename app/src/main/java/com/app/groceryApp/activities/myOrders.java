package com.app.groceryApp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.groceryApp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class myOrders extends AppCompatActivity implements myOrderAdapter.onOrderClickListener {

    RecyclerView orderRecycler;
    FirebaseFirestore store;
    int noOfOrders, i;
    TextView noOrder;
    RecyclerView.Adapter Adapter;
    List<Map<String, Object>> myOrdersList;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("My Orders");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        progressBar = findViewById(R.id.orderProgressBar);

        orderRecycler = findViewById(R.id.ordersRecycler);
        noOrder = findViewById(R.id.noOrderTxt);

        store = FirebaseFirestore.getInstance();

    }

    private void setOrdersRecycler() {
        orderRecycler.setHasFixedSize(true);
        orderRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        Adapter = new myOrderAdapter(myOrdersList, this);
        orderRecycler.setVisibility(View.VISIBLE);
        orderRecycler.setAdapter(Adapter);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        myOrdersList = new ArrayList<>();
        store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    noOfOrders = (int) (long) document.get("no of orders");

                    if (noOfOrders == 0) {
                        progressBar.setVisibility(View.GONE);
                        noOrder.setVisibility(View.VISIBLE);
                        orderRecycler.setVisibility(View.GONE);
                    } else {

                        store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("orders").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                myOrdersList.add(0, document.getData());


                                            }
                                            setOrdersRecycler();
                                        }
                                    }
                                });

                    }
                }
            }
        });
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

    @Override
    public void onOrderClick(int position) {
        Intent intent = new Intent(myOrders.this, currentOrderInfo.class);

        Map<String, Object> map = myOrdersList.get(position);
        Gson gson = new Gson();
        String jsonMap = gson.toJson(map);
        intent.putExtra("map", jsonMap);
        orderRecycler.setVisibility(View.GONE);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}