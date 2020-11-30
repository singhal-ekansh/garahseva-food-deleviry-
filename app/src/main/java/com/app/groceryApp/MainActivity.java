package com.app.groceryApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment;
    Toolbar toolbar;
    ArrayList<itemHelperClass> myCartList;
    int RC_SIGN_IN = 500;
    LinearLayout choseLocationLayout;
    Spinner spinner;
    boolean doubleBackToExitPressedOnce = false;
    String place;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.mainToolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        choseLocationLayout = findViewById(R.id.locationLayout);
        spinner = findViewById(R.id.deliverToSpinner);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        place = prefConfig.getDeliveryLocation(getApplicationContext());

        switch (place) {
            case "Vikasnagar":
                spinner.setSelection(0);
                break;
            case "Dakpatthar":
                spinner.setSelection(1);
                break;
            case "Herbertpur":
                spinner.setSelection(2);
                break;
        }

        //on re selecting navView item fragment should not load again
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        prefConfig.setDeliverLocation(getApplicationContext(), "Vikasnagar");
                        break;
                    case 1:
                        prefConfig.setDeliverLocation(getApplicationContext(), "Dakpatthar");
                        break;
                    case 2:
                        prefConfig.setDeliverLocation(getApplicationContext(), "Herbertpur");
                        break;

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new home_screen()).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);


    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.searchBtn) {
                Intent intent = new Intent(MainActivity.this, searchBar.class);

                startActivity(intent);
                overridePendingTransition(0, 0);
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


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    selectedFragment = null;


                    switch (item.getItemId()) {

                        case R.id.a:
                            selectedFragment = new home_screen();
                            choseLocationLayout.setVisibility(View.VISIBLE);
                            toolbar.setTitle("Home");
                            break;
                        case R.id.b:
                            selectedFragment = new offer_screen();
                            toolbar.setTitle("Offers");
                            choseLocationLayout.setVisibility(View.GONE);
                            break;
                        case R.id.c:
                            selectedFragment = new cart_screen();
                            choseLocationLayout.setVisibility(View.GONE);
                            toolbar.setTitle("My Cart");
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, selectedFragment).commit();

                    return true;
                }
            };

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
                    newUser.put("no of orders", 0);
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
    protected void onResume() {
        super.onResume();

        myCartList = prefConfig.getList(getApplicationContext());

        if (myCartList == null)
            myCartList = new ArrayList<>();

        if (myCartList.size() != 0)
            bottomNavigationView.getOrCreateBadge(R.id.c).setNumber(myCartList.size());
        else
            bottomNavigationView.removeBadge(R.id.c);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("myCartList")) {
            myCartList = prefConfig.getList(getApplicationContext());

            if (myCartList == null)
                myCartList = new ArrayList<>();

            if (myCartList.size() != 0)
                bottomNavigationView.getOrCreateBadge(R.id.c).setNumber(myCartList.size());
            else
                bottomNavigationView.removeBadge(R.id.c);

        }
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