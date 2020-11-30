package com.app.groceryApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class itemList extends AppCompatActivity implements itemAdapter.onItemListener {
    RecyclerView itemRecyclerView;
    RecyclerView.Adapter Adapter;
    ArrayList<itemHelperClass> itemList, myCartList;
    Toolbar toolbar;
    FirebaseFirestore store;
    String itemsJson,place;
    ProgressBar bar;
    boolean fromBanner;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        intent = getIntent();
        itemRecyclerView = findViewById(R.id.itemRecycler);
        toolbar = findViewById(R.id.itemToolbar);
        store = FirebaseFirestore.getInstance();
        bar = findViewById(R.id.itemProgressBar);
        bar.setVisibility(View.VISIBLE);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        toolbar.setTitle(intent.getStringExtra("subCategory"));

        place = prefConfig.getDeliveryLocation(getApplicationContext());
        fromBanner=intent.getBooleanExtra("fromBanner",false);

        myCartList = prefConfig.getList(getApplicationContext());

        if (myCartList == null)
            myCartList = new ArrayList<>();

        if (!fromBanner) {

            store.collection("admin").document(place).collection("products").document(intent.getStringExtra("category")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        itemsJson = document.getString(intent.getStringExtra("subCategory"));

                        itemList = prefConfig.getProducts(itemsJson);

                        if (itemList == null)
                            itemList = new ArrayList<>();

                        setItems();

                    }
                }
            });
        } else{
            itemsJson = intent.getStringExtra("bannerProducts");
            itemList = prefConfig.getProducts(itemsJson);
            setItems();
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setItems() {
        itemRecyclerView.setHasFixedSize(true);

        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        int i = 0;
        for (itemHelperClass helperClass1 : myCartList) {

            i = 0;
            for (itemHelperClass helperClass2 : itemList) {
                if (helperClass1.getItemName().equals(helperClass2.getItemName())) {
                    itemList.get(i).itemQuantity = helperClass1.getItemQuantity();
                    break;
                }
                i++;
            }

        }

        Adapter = new itemAdapter(itemList, this, getApplicationContext());
        itemRecyclerView.setAdapter(Adapter);
        bar.setVisibility(View.GONE);

    }


    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.searchBtn2) {
                Intent intent = new Intent(itemList.this, searchBar.class);

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            return true;
        }
    };


    @Override
    public void onAddBtnClick(int position) {


        myCartList.add(new itemHelperClass(itemList.get(position).getItemImage(), itemList.get(position).getItemName(), itemList.get(position).getItemPrice(), itemList.get(position).getDiscount(), 1));
        prefConfig.saveList(getApplicationContext(), myCartList);
    }

    @Override
    public void onIncreaseBtnClick(int position, int value) {
        int i = 0;

        for (itemHelperClass helperClass : myCartList) {

            if (helperClass.getItemName().equals(itemList.get(position).getItemName())) {
                myCartList.get(i).itemQuantity = value;
                break;
            }
            i++;
        }
        prefConfig.saveList(getApplicationContext(), myCartList);

    }

    @Override
    public void onDecreaseBtnClick(int position, int value) {

        int i = 0;

        for (itemHelperClass helperClass : myCartList) {

            if (helperClass.getItemName().equals(itemList.get(position).getItemName())) {
                if (value != 0)
                    myCartList.get(i).itemQuantity = value;
                else
                    myCartList.remove(i);

                break;
            }
            i++;
        }
        prefConfig.saveList(getApplicationContext(), myCartList);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}