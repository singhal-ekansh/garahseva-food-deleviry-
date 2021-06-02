package com.app.groceryApp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.groceryApp.R;
import com.app.groceryApp.utils.prefConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class checkoutPayment extends AppCompatActivity {
    int type;
    double orderAmount, delCharge, totalAmountToPay;
    RadioGroup payGroup;
    TextView deliveryAmountView, cartOrderAmountView, totalAmountView, walletTextView, walletCashAmtView, cashbackTextView, cashbackAmountView;
    Button placeOrderBtn, applyBtn;
    EditText deliveryNoteEdit, voucherEdit;
    FirebaseFirestore store;
    int currentOrderNo, allOrdersId, walletCash, cashback;
    String orderDetails, shipAddress, modeOfPayment = "COD", deliveryNote = "", place, appliedCode = "";
    AlertDialog alertDialog, progressDialog;
    Map<String, Object> codeDetails;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_payment);

        deliveryAmountView = findViewById(R.id.delValue);
        cartOrderAmountView = findViewById(R.id.cartValue);
        totalAmountView = findViewById(R.id.totalValue);
        payGroup = findViewById(R.id.paymentGroup);
        placeOrderBtn = findViewById(R.id.payBtn);
        walletCashAmtView = findViewById(R.id.walletCash);
        cashbackAmountView = findViewById(R.id.cashback);
        walletTextView = findViewById(R.id.text3);
        cashbackTextView = findViewById(R.id.text4);
        deliveryNoteEdit = findViewById(R.id.delNote);
        voucherEdit = findViewById(R.id.promoEdit);
        applyBtn = findViewById(R.id.addPromoBtn);


        store = FirebaseFirestore.getInstance();
        place = prefConfig.getDeliveryLocation(getApplicationContext());

        progressDialog();
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Checkout");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);

        orderDetails = prefConfig.getOrderJson(getApplicationContext());
        shipAddress = prefConfig.getAddressJson(getApplicationContext());

        intent = getIntent();
        type = intent.getIntExtra("type", 0);
        delCharge = intent.getDoubleExtra("delCharge", 0);
        orderAmount = intent.getDoubleExtra("cartAmount", 0);

        store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    walletCash = (int) (long) document.get("wallet");

                    totalAmountToPay = delCharge + orderAmount;

                    if (walletCash > totalAmountToPay) {
                        walletCash = (int) totalAmountToPay;
                        totalAmountToPay = 0;

                    } else {
                        totalAmountToPay -= walletCash;
                    }

                    if (walletCash != 0) {
                        walletTextView.setVisibility(View.VISIBLE);
                        walletCashAmtView.setText("- ₹ " + walletCash);
                        walletCashAmtView.setVisibility(View.VISIBLE);
                    }
                    deliveryAmountView.setText("₹ " + (int) delCharge);
                    cartOrderAmountView.setText("₹ " + orderAmount);
                    totalAmountView.setText("₹ " + (int) totalAmountToPay);
                }
                progressDialog.dismiss();
            }
        });


        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog();

                store.collection("admin").document(place).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            allOrdersId = (int) (long) document.get("order id");
                            allOrdersId++;
                            deliveryNote = deliveryNoteEdit.getText().toString();

                            if (payGroup.getCheckedRadioButtonId() == R.id.cashDel || payGroup.getCheckedRadioButtonId() == R.id.cardDel) {


                                store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            currentOrderNo = (int) (long) document.get("no of orders");
                                            currentOrderNo++;
                                            setNewOrder();
                                        }
                                    }
                                });

                            } else
                                progressDialog.dismiss();
                        } else
                            progressDialog.dismiss();
                    }
                });

            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voucherEdit.setError(null);
                progressDialog();

                String code;
                code = voucherEdit.getText().toString().trim();

                if (code.length() == 0) {
                    voucherEdit.setError("enter code");
                    progressDialog.dismiss();
                } else {
                    store.collection("admin").document(place).collection("vouchers").whereEqualTo("code", code)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        int flag = 0;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            flag = 1;
                                            codeDetails = document.getData();
                                            if (codeDetails.get("status").equals(true)) {
                                                if ((int) (long) codeDetails.get("minimum amount") <= (int) orderAmount)
                                                    checkUserOrders();
                                                else {
                                                    voucherEdit.setError("valid on minimum cart value " + (int) (long) codeDetails.get("minimum amount"));
                                                    progressDialog.dismiss();
                                                }
                                            } else {
                                                voucherEdit.setError("promo expired");
                                                progressDialog.dismiss();
                                            }
                                        }
                                        if (flag == 0) {
                                            voucherEdit.setError("invalid code");
                                            progressDialog.dismiss();
                                        }
                                    }
                                }
                            });
                }

            }
        });
    }

    private void checkUserOrders() {
        store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (codeDetails.get("only on first order").equals(true)) {
                        if ((int) (long) document.get("no of orders") == 0) {
                            checkUserVouchers();
                        } else {
                            voucherEdit.setError("only valid on first order");
                            progressDialog.dismiss();
                        }
                    } else
                        checkUserVouchers();

                }
            }
        });
    }

    private void checkUserVouchers() {
        store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("codes used")
                .whereEqualTo("code", codeDetails.get("code").toString()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int flag = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                flag = 1;
                            }
                            if (flag == 1) {
                                voucherEdit.setError("already applied");
                                progressDialog.dismiss();
                            } else
                                applyCashback();

                        }
                    }
                });
    }

    private void applyCashback() {

        if (!(boolean) codeDetails.get("free del")) {
            cashbackTextView.setVisibility(View.VISIBLE);
            cashbackAmountView.setText("₹ " + (int) (long) codeDetails.get("cashback"));
            cashbackAmountView.setVisibility(View.VISIBLE);
            cashback = (int) (long) codeDetails.get("cashback");
            delCharge = intent.getDoubleExtra("delCharge", 0);
        } else {
            cashbackTextView.setVisibility(View.GONE);
            cashbackAmountView.setVisibility(View.GONE);
            cashback = 0;
            delCharge = 0;
        }
        appliedCode = codeDetails.get("code").toString();
        deliveryAmountView.setText("₹ " + (int) delCharge);
        progressDialog.dismiss();
    }

    private void setNewOrder() {
        Map<String, Object> newOrder = new HashMap<>();
        Map<String, Object> newCode = new HashMap<>();
        newOrder.put("order id", allOrdersId);
        newOrder.put("order detail", orderDetails);
        newOrder.put("delivery address", shipAddress);
        newOrder.put("mode of payment", modeOfPayment);
        newOrder.put("total amount", orderAmount);
        newOrder.put("wallet cash", walletCash);
        newOrder.put("cashback", cashback);

        newOrder.put("placed on", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        newOrder.put("time", new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        newOrder.put("type", type);
        newOrder.put("delivery fee", (int) delCharge);
        newOrder.put("status", "on going");
        newOrder.put("user id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        newOrder.put("delivery note", deliveryNote);

        if (appliedCode.length() != 0) {
            newOrder.put("promo used", appliedCode);
            newCode.put("code", appliedCode);
            store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("codes used").document()
                    .set(newCode);
        }
        String userOrderNumber;
        if (currentOrderNo < 10)
            userOrderNumber = "0" + currentOrderNo;
        else
            userOrderNumber = Integer.toString(currentOrderNo);

        store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("orders").document(userOrderNumber)
                .set(newOrder);
        store.collection("admin").document(place).collection("current orders").document(String.valueOf(allOrdersId)).set(newOrder);

        store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("no of orders", currentOrderNo);
        store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("has current order", true);
        store.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("wallet", FieldValue.increment(-1 * walletCash));

        store.collection("admin").document(place).update("order id", FieldValue.increment(1));


        prefConfig.onPlacingOrder(getApplicationContext());
        progressDialog.dismiss();
        confirmedDialog();
        PrepareNotificationMessage();
    }

    private void confirmedDialog() {

        AlertDialog.Builder placedAlert = new AlertDialog.Builder(checkoutPayment.this);
        View view = getLayoutInflater().inflate(R.layout.confirm_order_dialog, null);

        Button continueShop, toOrders;

        continueShop = view.findViewById(R.id.continueBtn);
        toOrders = view.findViewById(R.id.toOrderBtn);

        placedAlert.setView(view);
        alertDialog = placedAlert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        alertDialog.show();

        continueShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });

        toOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startActivity(new Intent(checkoutPayment.this, myOrders.class));
                finish();

            }
        });

    }

    private void progressDialog() {
        AlertDialog.Builder progressAlert = new AlertDialog.Builder(checkoutPayment.this);
        View view = getLayoutInflater().inflate(R.layout.progress_dialog, null);

        progressAlert.setView(view);
        progressDialog = progressAlert.create();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        progressDialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

    public void onRadioSelect(View view) {


        if (payGroup.getCheckedRadioButtonId() == R.id.cashDel) {
            modeOfPayment = "COD";
            placeOrderBtn.setText("Place Order");

        } else if (payGroup.getCheckedRadioButtonId() == R.id.cardDel) {
            modeOfPayment = "card on delivery";
            placeOrderBtn.setText("Place Order");

        }

    }

    private void PrepareNotificationMessage() {
        String NOTIFICATION_TOPIC = "/topics/" + prefConfig.FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Order";
        String NOTIFICATION_MESSAGE = "You have a new order \nOrder Id:" + allOrdersId;
        String NOTIFICATION_TYPE = "NewOrder";

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);
            notificationBodyJo.put("deliverTo", place);
            notificationBodyJo.put("customerUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBodyJo);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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