package com.app.groceryApp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {

    RecyclerView recyclerView;
    TextView numberOfPlaces;
    FirebaseFirestore firebaseFirestore;
    String place;
    RestaurantAdapter adapter;
    ProgressBar progressBar;
    List<RestaurantData> restaurantDataList = new ArrayList<>();
    List<String> ids = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        recyclerView = view.findViewById(R.id.restaurantsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        progressBar = view.findViewById(R.id.progress_bar);
        numberOfPlaces = view.findViewById(R.id.numberOfPlaces);
        firebaseFirestore = FirebaseFirestore.getInstance();
        place = prefConfig.getDeliveryLocation(getContext());
        progressBar.setVisibility(View.VISIBLE);

        firebaseFirestore.collection("admin").document(place).collection("restaurants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                        if (snapshot.exists()) {
                            ids.add(snapshot.getId());
                            restaurantDataList.add(snapshot.toObject(RestaurantData.class));
                        }
                    }
                    setRestaurants();
                    Log.d("restaurants", new Gson().toJson(restaurantDataList));
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }

    private void setRestaurants() {
        numberOfPlaces.setText(restaurantDataList.size() + " Restaurants Near You");
        adapter = new RestaurantAdapter(restaurantDataList, ids ,getContext());
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }
}