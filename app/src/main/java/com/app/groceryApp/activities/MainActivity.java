package com.app.groceryApp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.app.groceryApp.R;
import com.app.groceryApp.fragments.FoodFragment;
import com.app.groceryApp.utils.BottomSheetLocation;
import com.app.groceryApp.utils.prefConfig;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment;
    Toolbar toolbar;
    int RC_SIGN_IN = 500, x = 0;
    LinearLayout choseLocationLayout;
    TextView locationText;
    boolean doubleBackToExitPressedOnce = false;
    String place, placeSelected;
    AlertDialog alertDialog;
    Button cancel;
    BottomSheetLocation bottomSheetLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.mainToolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar.setTitle("Food Delivery");
        locationText = findViewById(R.id.deliverToLocation);
        choseLocationLayout = findViewById(R.id.locationLayout);

        // bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomSheetLocation = new BottomSheetLocation();
        place = prefConfig.getDeliveryLocation(getApplicationContext());
        locationText.setText(place);

        choseLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetLocation.show(getSupportFragmentManager(), "tag");
            }
        });

        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new FoodFragment()).commit();

    }

    private final Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.searchBtn) {

                // Intent intent = new Intent(MainActivity.this, searchBar.class);

                //startActivity(intent);
                //overridePendingTransition(0, 0);
            } else if (item.getItemId() == R.id.myAcc) {

                FirebaseUser user;
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    //authUi open
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.PhoneBuilder().build());

                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);


                } else {
                    Intent intent = new Intent(MainActivity.this, myAccount.class);

                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
            return true;
        }
    };

/*
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    selectedFragment = null;


                    switch (item.getItemId()) {

                      case R.id.a:
                            selectedFragment = new GroceryFragment();
                            choseLocationLayout.setVisibility(View.VISIBLE);
                            toolbar.setTitle("Grocery");
                            break;


                        case R.id.aa:
                            selectedFragment = new FoodFragment();
                            choseLocationLayout.setVisibility(View.VISIBLE);
                            toolbar.setTitle("Food");
                            break;
                       case R.id.b:
                            selectedFragment = new offer_screen();
                            toolbar.setTitle("Offers");
                            choseLocationLayout.setVisibility(View.VISIBLE);
                            break;



                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, selectedFragment).commit();

                    return true;
                }
            };

 */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                if (response.isNewUser()) {
                    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
                    DocumentReference reference = fireStore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("has current order", false);
                    newUser.put("wallet", 0);
                    reference.set(newUser);
                }


                FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(new Intent(MainActivity.this, myAccount.class));
            } else {
                Toast.makeText(getApplicationContext(), "login not successful", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("deliveryArea")) {
            placeSelected = prefConfig.getDeliveryLocation(getApplicationContext());
            locationText.setText(placeSelected);
            if (!placeSelected.equals(place)) {

                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new FoodFragment()).commit();
                place = placeSelected;
                showAlert();
            }

        }
    }

    private void showAlert() {

        AlertDialog.Builder confirmAlert = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.on_location_change_dialog, null);


        cancel = view.findViewById(R.id.continueId);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        confirmAlert.setView(view);
        alertDialog = confirmAlert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(false);
        alertDialog.show();
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
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}