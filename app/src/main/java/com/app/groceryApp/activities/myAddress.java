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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.groceryApp.R;
import com.app.groceryApp.utils.prefConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Map;

public class myAddress extends AppCompatActivity {
    EditText nameView, phoneView, addressView, landmarkView;
    TextView placeView;
    Button submitBtn;
    String name, phone, postalCode, address, landmark = "";
    RadioButton radioButtonChecked, radioButtonOnHour, radioButtonOnDay, radioButtonNextDay;
    RadioGroup rGroup;
    AlertDialog progressDialog;
    FirebaseFirestore firebaseFirestore;
    int hour, day, nextDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Delivery Details");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);

        nameView = findViewById(R.id.editTextTextPersonName);
        phoneView = findViewById(R.id.editTextPhone);
        addressView = findViewById(R.id.editTextAddress);
        landmarkView = findViewById(R.id.editTextLandmark);
        submitBtn = findViewById(R.id.addAddressBtn);
        placeView = findViewById(R.id.postalCodeView);
        firebaseFirestore = FirebaseFirestore.getInstance();

        rGroup = findViewById(R.id.optionGroup);
        radioButtonOnHour = findViewById(R.id.onHour);
        radioButtonOnDay = findViewById(R.id.onDay);
        radioButtonNextDay = findViewById(R.id.nextDay);
        progressDialog();

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
        placeView.setText(postalCode);
        getSavedAddress();

        firebaseFirestore.collection("admin").document(place).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    hour = (int) (long) document.get("hour charge");
                    day = (int) (long) document.get("day charge");
                    nextDay = (int) (long) document.get("next day charge");
                    setDeliverySlots();
                }
            }
        });


        final Intent prevIntent = getIntent();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog();
                if (checkErrors()) {
                    prefConfig.saveAddress(getApplicationContext(), name, phone, postalCode, address, landmark);
                    radioButtonChecked = findViewById(rGroup.getCheckedRadioButtonId());

                    Intent intent = new Intent(myAddress.this, checkoutPayment.class);
                    if (radioButtonChecked.getId() == R.id.onHour) {
                        intent.putExtra("type", 1);
                        intent.putExtra("delCharge", (double) hour);
                    } else if (radioButtonChecked.getId() == R.id.onDay) {
                        intent.putExtra("type", 2);
                        intent.putExtra("delCharge", (double) day);
                    } else {
                        intent.putExtra("type", 3);
                        intent.putExtra("delCharge", (double) nextDay);
                    }

                    intent.putExtra("cartAmount", prevIntent.getDoubleExtra("cartValue", 0));
                    progressDialog.dismiss();
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

                } else
                    progressDialog.dismiss();

            }
        });


    }

    private void setDeliverySlots() {
        Calendar rightNow = Calendar.getInstance();
        radioButtonOnHour.setText("1 Hour delivery (Rs-" + hour + ")");
        radioButtonOnDay.setText("On Day delivery (Rs-" + day + ")");
        radioButtonNextDay.setText("Next Day delivery (Rs-" + nextDay + ")");


        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)

        if (currentHourIn24Format >= 19 || currentHourIn24Format <= 8) {
            radioButtonOnHour.setVisibility(View.GONE);
            radioButtonOnDay.setChecked(true);
        }
        if (currentHourIn24Format >= 16) {
            radioButtonOnDay.setVisibility(View.GONE);

            if (currentHourIn24Format >= 19)
                radioButtonNextDay.setChecked(true);
            else
                radioButtonOnHour.setChecked(true);

        }
        progressDialog.dismiss();
    }

    private void getSavedAddress() {
        Map<String, Object> addressMap = prefConfig.getAddressMap(getApplicationContext());

        if (addressMap != null) {
            nameView.setText(addressMap.get("name").toString());
            phoneView.setText(addressMap.get("contact").toString());
            addressView.setText(addressMap.get("delivery address").toString());
            landmarkView.setText(addressMap.get("landmark").toString());

        }
    }

    private void progressDialog() {
        AlertDialog.Builder progressAlert = new AlertDialog.Builder(myAddress.this);
        View view = getLayoutInflater().inflate(R.layout.progress_dialog, null);

        progressAlert.setView(view);
        progressDialog = progressAlert.create();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        progressDialog.show();

    }

    private boolean checkErrors() {

        name = nameView.getText().toString().trim();
        phone = phoneView.getText().toString().trim();
        address = addressView.getText().toString().trim();
        landmark = landmarkView.getText().toString().trim();

        if (name.length() == 0) {
            nameView.setError("can not be empty");
            return false;
        }
        if (phone.length() == 0) {
            phoneView.setError("can not be empty");
            return false;
        }

        if (address.length() == 0) {
            addressView.setError("can not be empty");
            return false;
        }

        if (phone.length() != 10) {
            phoneView.setError("not valid number");
            return false;
        }

        return true;
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