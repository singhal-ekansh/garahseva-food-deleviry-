package com.app.groceryApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantDetailActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    Map<String, List<RestaurantData>> categoryItemMap = new HashMap<>();
    RestaurantCategoryAdapter adapter;
    public String _id;
    MaterialCardView orderDetailsCard;
    TextView totalItems, totalAmount;
    int items, amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        recyclerView = findViewById(R.id.itemCategoryRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        firebaseFirestore = FirebaseFirestore.getInstance();
        _id = getIntent().getStringExtra("_id");
        orderDetailsCard = findViewById(R.id.orderDetail);
        totalAmount = findViewById(R.id.totalPrice);
        totalItems = findViewById(R.id.totalItems);


        firebaseFirestore.collection("restaurants").document(_id).collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                    RestaurantData data = doc.toObject(RestaurantData.class);

                    List<RestaurantData> list;
                    if (categoryItemMap.containsKey(data.getCategory())) {
                        list = categoryItemMap.get(data.getCategory());
                    } else {
                        list = new ArrayList<>();
                    }
                    list.add(data);
                    categoryItemMap.put(data.getCategory(), list);

                }
                setCategoryAndItems();
            }
        });
    }

    private void setCategoryAndItems() {
        adapter = new RestaurantCategoryAdapter(categoryItemMap, this);
        recyclerView.setAdapter(adapter);

        if (prefConfig.getCurrentRestaurant(getApplicationContext()).equals(_id))
            setOrderDetailsCard();
    }

    @Override
    protected void onStart() {
        super.onStart();
        prefConfig.registerPref(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefConfig.unregisterPref(this, this);
    }

    public void setOrderDetailsCard() {
        items = 0;
        amount = 0;
        List<RestaurantData> orderData = prefConfig.getFoodOrderList(getApplicationContext());
        if (orderData.size() == 0) {
            orderDetailsCard.setVisibility(View.GONE);
        } else {
            orderDetailsCard.setVisibility(View.VISIBLE);
            for (RestaurantData item : orderData) {
                items += Integer.parseInt(item.getQuantitySelected());
                amount += (Integer.parseInt(item.getPrice()) * Integer.parseInt(item.getQuantitySelected()));
            }
            totalItems.setText(items + " Items");
            totalAmount.setText("₹ " + amount);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        setOrderDetailsCard();
        Log.d("orders", new Gson().toJson(prefConfig.getFoodOrderList(getApplicationContext())));

    }
}