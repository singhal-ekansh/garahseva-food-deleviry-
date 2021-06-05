package com.app.groceryApp.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.groceryApp.BuildConfig;
import com.app.groceryApp.R;
import com.app.groceryApp.utils.prefConfig;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;

public class myAccount extends AppCompatActivity {

    FirebaseUser user;
    FirebaseFirestore fireStore;
    MaterialCardView logoutBtn, toMyOrderBtn, rateBtn, contactUs, addressToggle,shareBtn;
    ImageView editAddress;
    String postalCode, contact;
    LinearLayout addressLayout;
    TextView signInAs, accName, accNumber, accAddress, accLandmark, accPostal, myWalletAmt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setTitle("My Account");
        user = FirebaseAuth.getInstance().getCurrentUser();
        fireStore = FirebaseFirestore.getInstance();
        signInAs = findViewById(R.id.identifiedAs);
        toMyOrderBtn = findViewById(R.id.toMyOrders);
        logoutBtn = findViewById(R.id.logout);
        accName = findViewById(R.id.NameInAdd);
        accNumber = findViewById(R.id.NumberInAdd);
        accAddress = findViewById(R.id.AddressInAdd);
        accLandmark = findViewById(R.id.LandmarkInAdd);
        accPostal = findViewById(R.id.PostalInAdd);
        myWalletAmt = findViewById(R.id.walletAmt);
        rateBtn = findViewById(R.id.rateAppBtn);
        contactUs = findViewById(R.id.contactUsBtn);
        shareBtn = findViewById(R.id.shareBtn);
        editAddress = findViewById(R.id.addressEdit);
        addressToggle = findViewById(R.id.viewAddress);
        addressLayout = findViewById(R.id.deliveryAddLayout);
        signInAs.setText("User : " + user.getPhoneNumber());


        String place = prefConfig.getDeliveryLocation(getApplicationContext());

        switch (place) {
            case "Vikasnagar":
                postalCode = "248198";
                break;
            case "Dakpatthar":
                postalCode = "248125";
                break;
            case "Herbertpur":
                postalCode = "248142";
                break;
        }
        setAddressInMyAcc();


        fireStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    myWalletAmt.setText("₹ " + document.get("wallet"));
                }
            }
        });
        fireStore.collection("admin").document(place).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    contact = document.get("contact").toString();
                }
            }
        });


        toMyOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(myAccount.this, myOrders.class));
                overridePendingTransition(0, 0);
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  prefConfig.removeHasCurrentOrder(getApplicationContext());
                FirebaseMessaging.getInstance().unsubscribeFromTopic(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AuthUI.getInstance()
                                .signOut(getApplicationContext())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {

                                        finish();
                                        Toast.makeText(getApplicationContext(), "logout successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });


            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "First online food delivery platform in Vikasnagar. Get food delivered at your doorstep : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateApp();
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contact));
                startActivity(intent);
            }
        });

        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editDialog();
            }
        });

        addressToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addressLayout.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(addressToggle);
                    addressLayout.setVisibility(View.VISIBLE);
                } else
                    addressLayout.setVisibility(View.GONE);
            }
        });
    }

    private void editDialog() {
        final AlertDialog editAlertDialog;
        AlertDialog.Builder editAlert = new AlertDialog.Builder(myAccount.this);
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

        postalView.setText(postalCode);


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


                prefConfig.saveAddress(getApplicationContext(), name, phone, postalCode, address, landmark);
                editAlertDialog.dismiss();
                setAddressInMyAcc();
            }
        });

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
            accPostal.setText("Postal code:  " + "248198");
            accName.setText("Name : ");
            accNumber.setText("Contact : ");
            accAddress.setText("Address : ");
            accLandmark.setText("Landmark : ");
        }
    }


    //To play store to Rate app
    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);

        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        intent.addFlags(flags);
        return intent;
    }
//

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


}