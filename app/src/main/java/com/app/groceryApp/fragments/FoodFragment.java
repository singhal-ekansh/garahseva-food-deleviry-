package com.app.groceryApp.fragments;

import android.content.Intent;
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

import com.app.groceryApp.R;
import com.app.groceryApp.restaurants.RestaurantAdapter;
import com.app.groceryApp.restaurants.RestaurantData;
import com.app.groceryApp.restaurants.RestaurantDetailActivity;
import com.app.groceryApp.utils.prefConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FoodFragment extends Fragment {

    RecyclerView recyclerView;
    TextView numberOfPlaces, orderResName;
    FirebaseFirestore firebaseFirestore;
    String place;
    RestaurantAdapter adapter;
    ProgressBar progressBar;
    List<RestaurantData> restaurantDataList = new ArrayList<>();
    List<String> ids = new ArrayList<>();
    MaterialCardView savedOrderCard;
    int i;

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
        savedOrderCard = view.findViewById(R.id.orderDetail);
        orderResName = view.findViewById(R.id.savedResName);

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
        savedOrderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RestaurantDetailActivity.class);
                intent.putExtra("_id", ids.get(i));
                intent.putExtra("res_name", restaurantDataList.get(i).getName());
                startActivityForResult(intent,1);
            }
        });
        return view;
    }

    private void setRestaurants() {
        numberOfPlaces.setText(restaurantDataList.size() + " Restaurants Near You");
        adapter = new RestaurantAdapter(restaurantDataList, ids, getContext(),this);
        recyclerView.setAdapter(adapter);
        setOrderDetailsCard();
        progressBar.setVisibility(View.GONE);

    }

    private void setOrderDetailsCard() {
        if (!prefConfig.getCurrentRestaurant(getContext()).equals("")) {
            savedOrderCard.setVisibility(View.VISIBLE);
            i = 0;
            for (String id : ids) {
                if (prefConfig.getCurrentRestaurant(getContext()).equals(id)) {
                    break;
                }
                i++;
            }
            orderResName.setText(restaurantDataList.get(i).getName());
        } else
            savedOrderCard.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK)
            setOrderDetailsCard();
    }
}