package com.app.groceryApp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.groceryApp.R;
import com.app.groceryApp.restaurants.RestaurantData;
import com.app.groceryApp.restaurants.RestaurantItemsAdapter;
import com.app.groceryApp.utils.prefConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    RecyclerView recyclerView;
    RestaurantItemsAdapter adapter;
    TextView itemTotalAmount, grandTotalAmount, accName, accNumber, accAddress, accLandmark, accPostal, resName;
    int AmountTotal;
    MaterialCardView placeBtn;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        recyclerView = findViewById(R.id.orderItemsRecycler);
        itemTotalAmount = findViewById(R.id.itemTotalAmount);
        grandTotalAmount = findViewById(R.id.grandTotalAmount);
        accName = findViewById(R.id.NameInAdd);
        accNumber = findViewById(R.id.NumberInAdd);
        accAddress = findViewById(R.id.AddressInAdd);
        accLandmark = findViewById(R.id.LandmarkInAdd);
        accPostal = findViewById(R.id.PostalInAdd);
        resName = findViewById(R.id.resName);
        placeBtn = findViewById(R.id.placeOrderBtn);
        firebaseFirestore = FirebaseFirestore.getInstance();
        resName.setText("from : " + getIntent().getStringExtra("restaurant_name"));
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setTitle("Checkout");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new RestaurantItemsAdapter(prefConfig.getFoodOrderList(getApplicationContext()), this, true);
        recyclerView.setAdapter(adapter);

        setTotalAmounts();
        setAddressInMyAcc();

        placeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> newOrder = new HashMap<>();
                newOrder.put("delivery address", prefConfig.getAddressJson(getApplicationContext()));
                newOrder.put("order detail", new Gson().toJson(prefConfig.getFoodOrderList(getApplicationContext())));
                newOrder.put("total amount", String.valueOf(AmountTotal));
                newOrder.put("user id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                newOrder.put("timestamp", String.valueOf((new Date().getTime()) / 1000));
                newOrder.put("status", "on going");

                firebaseFirestore.collection("restaurants").document(getIntent().getStringExtra("restaurant_id")).collection("orders").add(newOrder).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            DocumentReference documentReference = task.getResult();
                            newOrder.put("restaurant id", getIntent().getStringExtra("restaurant_id"));
                            newOrder.remove("user id");
                            firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("orders").document(documentReference.getId()).set(newOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("has current order", true);
                                        prefConfig.clearFoodOrderList(getApplicationContext());
                                        prefConfig.changeHasCurrentOrder(getApplicationContext(), true);
                                        setResult(10);
                                        PrepareNotificationMessage();
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    private void setTotalAmounts() {
        AmountTotal = 0;
        List<RestaurantData> orderData = prefConfig.getFoodOrderList(getApplicationContext());
        if (orderData.size() != 0) {
            for (RestaurantData item : orderData) {
                AmountTotal += (Integer.parseInt(item.getPrice()) * Integer.parseInt(item.getQuantitySelected()));
            }
        }
        itemTotalAmount.setText("₹ " + AmountTotal);
        grandTotalAmount.setText("₹ " + AmountTotal);
    }

    private void setAddressInMyAcc() {
        Map<String, Object> addressMap = prefConfig.getAddressMap(getApplicationContext());

        if (addressMap != null) {
            accName.setText("Name : " + addressMap.get("name").toString());
            accNumber.setText("Contact : " + addressMap.get("contact").toString());
            accPostal.setText("Postal code : " + "248198");
            accAddress.setText("Address : " + addressMap.get("delivery address").toString());
            accLandmark.setText("Landmark : " + addressMap.get("landmark").toString());
        } else {
            accPostal.setText("Postal code : " + "248198");
            accName.setText("Name : ");
            accNumber.setText("Contact : ");
            accAddress.setText("Address : ");
            accLandmark.setText("Landmark : ");
        }
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        setTotalAmounts();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

    private void PrepareNotificationMessage() {
        String NOTIFICATION_TOPIC = "/topics/" + prefConfig.FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Order";
        String NOTIFICATION_MESSAGE = "You have a new order";
        String NOTIFICATION_TYPE = "NewOrder";

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);
            notificationBodyJo.put("deliverTo", "");
            notificationBodyJo.put("customerUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBodyJo);

        } catch (Exception e) {

        }
        sendFcmNotificationToAdmin(notificationJo);

    }

    private void sendFcmNotificationToAdmin(JSONObject notificationJo) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Key=" + prefConfig.FCM_KEY);

                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }
}