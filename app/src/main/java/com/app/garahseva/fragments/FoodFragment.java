package com.app.garahseva.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.garahseva.R;
import com.app.garahseva.activities.myOrders;
import com.app.garahseva.restaurants.RestaurantAdapter;
import com.app.garahseva.restaurants.RestaurantData;
import com.app.garahseva.restaurants.RestaurantDetailActivity;
import com.app.garahseva.utils.prefConfig;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class FoodFragment extends Fragment {

    RecyclerView recyclerView;
    TextView numberOfPlaces, orderResName,currentOrder,comingSoonTxt;
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
        comingSoonTxt = view.findViewById(R.id.comingSoonTxt);
        firebaseFirestore = FirebaseFirestore.getInstance();
        place = prefConfig.getDeliveryLocation(getContext());
        progressBar.setVisibility(View.VISIBLE);
        savedOrderCard = view.findViewById(R.id.orderDetail);
        orderResName = view.findViewById(R.id.savedResName);
        currentOrder = view.findViewById(R.id.currentOrder);
        adapter = new RestaurantAdapter(restaurantDataList, ids, getContext(), this);
        recyclerView.setAdapter(adapter);
        comingSoonTxt.setVisibility(View.GONE);
        firebaseFirestore.collection("admin").document(place).collection("restaurants").orderBy("isClosed").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                restaurantDataList.clear();
                ids.clear();
                if (!value.isEmpty()) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
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
                if (restaurantDataList.get(i).getIsClosed()) {
                    Toast.makeText(getContext(), "Restaurant is closed", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getContext(), RestaurantDetailActivity.class);
                intent.putExtra("_id", ids.get(i));
                intent.putExtra("res_name", restaurantDataList.get(i).getName());
                intent.putExtra("del_fee", restaurantDataList.get(i).getDeliveryFee());
                intent.putExtra("free_del", restaurantDataList.get(i).getFreeDeliveryPrice());
                startActivityForResult(intent, 1);
            }
        });
        currentOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(), myOrders.class),1);
            }
        });
        return view;
    }

    private void setRestaurants() {
        numberOfPlaces.setText(restaurantDataList.size() + " Restaurants Around You");
        adapter.notifyDataSetChanged();
        setOrderDetailsCard();
        progressBar.setVisibility(View.GONE);
        comingSoonTxt.setVisibility(View.VISIBLE);

    }

    private void setOrderDetailsCard() {
        if(prefConfig.hasCurrentOrder(getContext())){
            currentOrder.setVisibility(View.VISIBLE);
        }else
            currentOrder.setVisibility(View.GONE);

        if (!prefConfig.getCurrentRestaurant(getContext()).equals("")) {
            i = 0;
            for (String id : ids) {
                if (prefConfig.getCurrentRestaurant(getContext()).equals(id)) {
                    break;
                }
                i++;
            }
            orderResName.setText(restaurantDataList.get(i).getName());
            if (restaurantDataList.get(i).getIsClosed())
                savedOrderCard.setVisibility(View.GONE);
            else
                savedOrderCard.setVisibility(View.VISIBLE);
        } else {
            savedOrderCard.setVisibility(View.GONE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK)
            setOrderDetailsCard();
    }
}