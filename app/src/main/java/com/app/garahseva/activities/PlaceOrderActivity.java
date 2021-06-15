package com.app.garahseva.activities;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.garahseva.R;
import com.app.garahseva.restaurants.RestaurantData;
import com.app.garahseva.restaurants.RestaurantItemsAdapter;
import com.app.garahseva.utils.Dialogs;
import com.app.garahseva.utils.prefConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    RecyclerView recyclerView;
    RestaurantItemsAdapter adapter;
    TextView itemTotalAmount, grandTotalAmount, accName, accNumber, accAddress, accLandmark, accPostal, resName, delFeeView;
    int AmountTotal, delFee;
    MaterialCardView placeBtn;
    FirebaseFirestore firebaseFirestore;
    ImageView addressEdit;
    Dialogs loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        recyclerView = findViewById(R.id.orderItemsRecycler);
        itemTotalAmount = findViewById(R.id.itemTotalAmount);
        grandTotalAmount = findViewById(R.id.grandTotalAmount);
        delFeeView = findViewById(R.id.delFeeView);
        accName = findViewById(R.id.NameInAdd);
        accNumber = findViewById(R.id.NumberInAdd);
        accAddress = findViewById(R.id.AddressInAdd);
        accLandmark = findViewById(R.id.LandmarkInAdd);
        accPostal = findViewById(R.id.PostalInAdd);
        resName = findViewById(R.id.resName);
        placeBtn = findViewById(R.id.placeOrderBtn);
        addressEdit = findViewById(R.id.addressEdit);
        loadingDialog = new Dialogs(PlaceOrderActivity.this);
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
                Map<String, Object> addressMap = prefConfig.getAddressMap(getApplicationContext());
                if (addressMap == null || addressMap.get("name").toString().equals("") || addressMap.get("contact").toString().equals("") || addressMap.get("delivery address").toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Complete Address Details", Toast.LENGTH_LONG).show();
                    editDialog();
                    return;
                }
                loadingDialog.showLoading();

                Map<String, Object> newOrder = new HashMap<>();
                newOrder.put("delivery_address", prefConfig.getAddressJson(getApplicationContext()));
                newOrder.put("order_detail", new Gson().toJson(prefConfig.getFoodOrderList(getApplicationContext())));
                newOrder.put("total_amount", String.valueOf(AmountTotal));
                newOrder.put("del_fee", String.valueOf(delFee));
                newOrder.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                newOrder.put("timestamp", String.valueOf((new Date().getTime()) / 1000));
                newOrder.put("status", "pending");

                firebaseFirestore.collection("restaurants").document(getIntent().getStringExtra("restaurant_id")).collection("orders").add(newOrder).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            DocumentReference documentReference = task.getResult();
                            newOrder.put("restaurant_id", getIntent().getStringExtra("restaurant_id"));
                            newOrder.put("restaurant_name", getIntent().getStringExtra("restaurant_name"));
                            newOrder.remove("user_id");
                            firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("orders").document(documentReference.getId()).set(newOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("has current order", true);
                                        prefConfig.clearFoodOrderList(getApplicationContext());
                                        prefConfig.changeHasCurrentOrder(getApplicationContext(), true);
                                        PrepareNotificationMessage();
                                        loadingDialog.cancelLoading();
                                        showPlacedOrder();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                setResult(10);
                                                finish();
                                            }
                                        }, 1200);
                                    } else
                                        loadingDialog.cancelLoading();
                                }
                            });
                        } else
                            loadingDialog.cancelLoading();
                    }
                });

            }
        });

        addressEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDialog();
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
        delFee = Integer.parseInt(getIntent().getStringExtra("del_fee"));
        if (AmountTotal < Integer.parseInt(getIntent().getStringExtra("free_del"))) {
            grandTotalAmount.setText("₹ " + (AmountTotal + delFee));
            delFeeView.setText("₹ " + delFee);
        } else {
            grandTotalAmount.setText("₹ " + AmountTotal);
            delFeeView.setText("FREE");
            delFee = 0;
        }
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
            accPostal.setText("Postal code : ");
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

    private void editDialog() {
        final AlertDialog editAlertDialog;
        AlertDialog.Builder editAlert = new AlertDialog.Builder(PlaceOrderActivity.this);
        View view = getLayoutInflater().inflate(R.layout.edit_address_dialog, null);
        final EditText dName, dNumber, dAddress, dLandmark;
        Button dButton;
        TextView postalView;


        dName = view.findViewById(R.id.dialog_name);
        dNumber = view.findViewById(R.id.dialog_number);
        dAddress = view.findViewById(R.id.dialog_address);
        dLandmark = view.findViewById(R.id.dialog_landmark);
        dButton = view.findViewById(R.id.dialog_editAddBtn);
        postalView = view.findViewById(R.id.dialog_postal);

        Map<String, Object> addressMap = prefConfig.getAddressMap(getApplicationContext());
        if (addressMap != null) {
            dName.setText(addressMap.get("name").toString());
            dNumber.setText(addressMap.get("contact").toString());
            dAddress.setText(addressMap.get("delivery address").toString());
            dLandmark.setText(addressMap.get("landmark").toString());
        }
        postalView.setText("248198");


        editAlert.setView(view);
        editAlertDialog = editAlert.create();
        editAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        editAlertDialog.setCanceledOnTouchOutside(true);
        editAlertDialog.setCancelable(true);
        editAlertDialog.show();


        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = dName.getText().toString().trim();
                String phone = dNumber.getText().toString().trim();
                String address = dAddress.getText().toString().trim();
                String landmark = dLandmark.getText().toString().trim();

                if (name.length() == 0) {
                    dName.setError("can not be empty");
                    return;
                }
                if (phone.length() == 0) {
                    dNumber.setError("can not be empty");
                    return;
                }

                if (address.length() == 0) {
                    dAddress.setError("can not be empty");
                    return;
                }

                if (phone.length() != 10) {
                    dNumber.setError("not valid number");
                    return;
                }


                prefConfig.saveAddress(getApplicationContext(), name, phone, "248198", address, landmark);
                editAlertDialog.dismiss();
                setAddressInMyAcc();
            }
        });

    }

    private void showPlacedOrder() {
        final AlertDialog placedAlertDialog;
        AlertDialog.Builder placedAlert = new AlertDialog.Builder(PlaceOrderActivity.this);
        View view = getLayoutInflater().inflate(R.layout.success_dialog, null);

        placedAlert.setView(view);
        placedAlertDialog = placedAlert.create();
        placedAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        placedAlertDialog.setCanceledOnTouchOutside(false);
        placedAlertDialog.setCancelable(false);
        placedAlertDialog.show();

    }

    private void PrepareNotificationMessage() {
        String NOTIFICATION_TOPIC = "/topics/" + getIntent().getStringExtra("restaurant_id");
        String NOTIFICATION_TITLE = "New Order";
        String NOTIFICATION_MESSAGE = "You have a new order";
        String NOTIFICATION_TYPE = "NewOrder";

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);
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