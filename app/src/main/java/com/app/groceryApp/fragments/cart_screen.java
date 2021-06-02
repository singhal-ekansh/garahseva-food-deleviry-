package com.app.groceryApp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.groceryApp.R;
import com.app.groceryApp.activities.cartAdapter;
import com.app.groceryApp.groceries.itemHelperClass;
import com.app.groceryApp.activities.myAddress;
import com.app.groceryApp.utils.prefConfig;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class cart_screen extends Fragment implements com.app.groceryApp.activities.cartAdapter.onCartItemClickListener {

    ArrayList<itemHelperClass> cartList;
    RecyclerView cartRecyclerView;
    RecyclerView.Adapter cartAdapter;
    View view;
    RelativeLayout checkoutView;
    TextView totalAmount;
    LinearLayout emptyText;
    double amount;
    Button checkout;
    private final int RC_SIGN_IN = 500;
    AlertDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cart_screen, container, false);
        cartRecyclerView = view.findViewById(R.id.cartRecycler);
        checkoutView = view.findViewById(R.id.checkoutLayout);
        totalAmount = view.findViewById(R.id.totalAmt);
        emptyText = view.findViewById(R.id.emptyWarning);
        emptyText.setVisibility(View.GONE);
        checkout = view.findViewById(R.id.checkoutBtn);



        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                if (firebaseAuth.getCurrentUser() != null) {

                    if (amount < 500) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Cart Amount should be at least 500", Toast.LENGTH_LONG).show();
                    } else {
                        FirebaseFirestore.getInstance().collection("users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.getBoolean("has current order")) {
                                        Toast.makeText(getActivity(), "can not place two orders at a time", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    } else {
                                        Intent intent = new Intent(getActivity(), myAddress.class);
                                        intent.putExtra("cartValue", amount);
                                        progressDialog.dismiss();
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(0,0);
                                    }

                                } else
                                    progressDialog.dismiss();
                            }
                        });

                    }
                } else {
                    progressDialog.dismiss();
                    List<AuthUI.IdpConfig> providers = Arrays.asList(

                            new AuthUI.IdpConfig.PhoneBuilder().build());

                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        });

        return view;
    }

    private void progressDialog() {
        AlertDialog.Builder progressAlert = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.progress_dialog, null);

        progressAlert.setView(view);
        progressDialog = progressAlert.create();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        progressDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {

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

            } else {
                Toast.makeText(getContext(), "login not successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setList(Context context) {
        progressDialog();
        cartRecyclerView.setHasFixedSize(true);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        cartList = prefConfig.getList(context);
        if (cartList == null)
            cartList = new ArrayList<>();


        if (cartList.size() == 0) {

            checkoutView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {

            checkoutView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
            amount = 0;
            for (itemHelperClass helperClass : cartList) {
                amount += (helperClass.getItemPrice() - helperClass.getItemPrice() * (helperClass.getDiscount() * 0.01)) * helperClass.getItemQuantity();
            }
            totalAmount.setText("₹ " + amount);


        }
        progressDialog.dismiss();

        cartAdapter = new cartAdapter(cartList, this, getContext());
        cartRecyclerView.setAdapter(cartAdapter);
    }

    @Override
    public void onPlusBtnClick(int position) {
        int qty = cartList.get(position).getItemQuantity();
        qty++;
        cartList.get(position).itemQuantity = qty;
        prefConfig.saveList(getContext(), cartList);
        setList(getContext());
    }

    @Override
    public void onMinusBtnClick(int position) {
        int qty = cartList.get(position).getItemQuantity();
        if (qty == 1) {
            cartList.remove(position);
        } else {
            qty--;
            cartList.get(position).itemQuantity = qty;
        }
        prefConfig.saveList(getContext(), cartList);
        setList(getContext());

    }

    @Override
    public void onResume() {
        super.onResume();
        setList(getContext());
    }
}