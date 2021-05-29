package com.app.groceryApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantDetailActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    Map<String, List<RestaurantData>> categoryItemMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        recyclerView = findViewById(R.id.itemCategoryRecycler);
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("restaurants").document(getIntent().getStringExtra("_id")).collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {

                    RestaurantData data = doc.toObject(RestaurantData.class);

                    List<RestaurantData> list;
                    if (categoryItemMap.containsKey(data.getCategory())) {
                        list = categoryItemMap.get(data.getCategory());
                    } else {
                        list = new ArrayList<>();
                    }
                    list.add(data);
                    categoryItemMap.put(data.getCategory(), list);

                }
                Log.d("restaurants", new Gson().toJson(categoryItemMap));
            }
        });
    }
}