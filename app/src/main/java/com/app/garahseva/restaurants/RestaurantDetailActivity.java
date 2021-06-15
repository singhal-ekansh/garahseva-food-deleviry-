package com.app.garahseva.restaurants;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.garahseva.R;
import com.app.garahseva.activities.PlaceOrderActivity;
import com.app.garahseva.utils.prefConfig;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
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
    ProgressBar progressBar;

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
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setTitle(getIntent().getStringExtra("res_name"));

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

        orderDetailsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                if (firebaseAuth.getCurrentUser() != null) {
                    if (prefConfig.hasCurrentOrder(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(),"Can not place multiple orders at once",Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(RestaurantDetailActivity.this, PlaceOrderActivity.class);
                    intent.putExtra("restaurant_name", getIntent().getStringExtra("res_name"));
                    intent.putExtra("restaurant_id", getIntent().getStringExtra("_id"));
                    intent.putExtra("del_fee", getIntent().getStringExtra("del_fee"));
                    intent.putExtra("free_del", getIntent().getStringExtra("free_del"));
                    startActivityForResult(intent, 1);
                } else {
                    List<AuthUI.IdpConfig> providers = Arrays.asList(

                            new AuthUI.IdpConfig.PhoneBuilder().build());

                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            500);
                }

            }
        });

    }

    private void setCategoryAndItems() {
        adapter = new RestaurantCategoryAdapter(categoryItemMap, this);
        recyclerView.setAdapter(adapter);

        if (prefConfig.getCurrentRestaurant(getApplicationContext()).equals(_id))
            setOrderDetailsCard();

        progressBar.setVisibility(View.GONE);
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
            if (amount < Integer.parseInt(getIntent().getStringExtra("free_del")))
                totalAmount.setText("₹ " + amount + "  (₹" + (Integer.parseInt(getIntent().getStringExtra("free_del")) - amount) + " more for FREE delivery)");
            else
                totalAmount.setText("₹ " + amount + "  (FREE delivery)");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        setOrderDetailsCard();
        Log.d("orders", new Gson().toJson(prefConfig.getFoodOrderList(getApplicationContext())));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK)
            setCategoryAndItems();

        if (resultCode == 10) {
            setResult(RESULT_OK);
            finish();
        }
        if (requestCode == 500) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {

                if (response.isNewUser()) {
                    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
                    DocumentReference reference = fireStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("has current order", false);
                    newUser.put("wallet", 0);
                    reference.set(newUser);
                }
                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getCurrentUser().getUid());

            } else {
                Toast.makeText(getApplicationContext(), "login not successful", Toast.LENGTH_SHORT).show();
            }
        }
    }
}