package com.app.groceryApp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.groceryApp.R;
import com.app.groceryApp.activities.VoucherAdapter;
import com.app.groceryApp.activities.VoucherHelperClass;
import com.app.groceryApp.utils.prefConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class offer_screen extends Fragment {

    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    String place;
    List<VoucherHelperClass> voucherList;
    TextView codeTextView, noOfferView;
    ProgressBar bar;
    VoucherAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_screen, container, false);
        recyclerView = view.findViewById(R.id.offerPageRecycler);
        codeTextView = view.findViewById(R.id.tex1);
        noOfferView = view.findViewById(R.id.noOffer);
        bar = view.findViewById(R.id.offerProgressBar);
        firebaseFirestore = FirebaseFirestore.getInstance();
        place = prefConfig.getDeliveryLocation(getContext());

        bar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("admin").document(place).collection("vouchers").whereEqualTo("status", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int flag = 0;
                            voucherList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                flag++;
                                voucherList.add(new VoucherHelperClass(document.get("code").toString(), document.get("details").toString()));
                            }
                            if (flag == 0) {
                                codeTextView.setVisibility(View.GONE);
                                noOfferView.setVisibility(View.VISIBLE);
                                bar.setVisibility(View.GONE);
                            } else {
                                codeTextView.setVisibility(View.VISIBLE);
                                noOfferView.setVisibility(View.GONE);
                                setVouchers();
                            }

                        }
                    }
                });


        return view;

    }

    private void setVouchers() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new VoucherAdapter(getContext(),voucherList);
        recyclerView.setAdapter(adapter);
        bar.setVisibility(View.GONE);
    }
}