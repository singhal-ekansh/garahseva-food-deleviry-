package com.app.groceryApp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class myOrders extends AppCompatActivity implements myOrderAdapter.onOrderClickListener {

    RecyclerView orderRecycler;
    FirebaseFirestore store;
    TextView noOrder;
    myOrderAdapter Adapter;
    List<OrderDetailClass> myOrdersList;
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
        orderRecycler.setHasFixedSize(true);
        orderRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        noOrder = findViewById(R.id.noOrderTxt);

        store = FirebaseFirestore.getInstance();

        store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("orders")
                .orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && !value.isEmpty()) {
                    myOrdersList = new ArrayList<>();
                    int i = 0;
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        myOrdersList.add(documentSnapshot.toObject(OrderDetailClass.class));
                        myOrdersList.get(i).setOrder_id(documentSnapshot.getId());
                        i++;
                    }
                    Adapter = new myOrderAdapter(myOrdersList, myOrders.this);
                    orderRecycler.setAdapter(Adapter);
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

        Gson gson = new Gson();
        String jsonMap = gson.toJson(myOrdersList.get(position));
        intent.putExtra("map", jsonMap);
        startActivity(intent);

    }
}