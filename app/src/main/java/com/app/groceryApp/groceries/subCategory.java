package com.app.groceryApp.groceries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.app.groceryApp.R;
import com.app.groceryApp.utils.prefConfig;
import com.app.groceryApp.activities.searchBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class subCategory extends AppCompatActivity implements subCategoryAdapter.onSubCategoryListener, HorizontalCardItemAdapter.onHorizontalItemListener {

    RecyclerView categoryGridRecycler, bestOfferRecycler;
    RecyclerView.Adapter adapter, bestOfferAdapter;
    ArrayList<categoryHelperClass> categoryList;
    ArrayList<itemHelperClass> BestItemList, myCartList, allCategoryItems;
    Intent intent;
    Toolbar toolbar;
    String category, itemsJson, place;
    FirebaseFirestore firebaseFirestore;
    ProgressBar pBar;

    TextView bestOfferTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        intent = getIntent();
        toolbar = findViewById(R.id.subCatToolbar);
        pBar = findViewById(R.id.BestOfferProgress);

        place = prefConfig.getDeliveryLocation(getApplicationContext());
        category = intent.getStringExtra("title");
        toolbar.setTitle(category);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        firebaseFirestore = FirebaseFirestore.getInstance();
        bestOfferRecycler = findViewById(R.id.subCategoryBestOfferRecycler);
        categoryGridRecycler = findViewById(R.id.grocery_category_recycler);

        bestOfferTextView = findViewById(R.id.bestOffersText);

        bestOfferTextView.setVisibility(View.GONE);

        setMainRecycler();


    }


    public void setMainRecycler() {
        categoryGridRecycler.setHasFixedSize(true);

        categoryGridRecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3, GridLayoutManager.VERTICAL, false));

        categoryList = new ArrayList<>();
        if (category.equals("Dry & Baking Goods")) {
            categoryList.add(new categoryHelperClass(R.drawable.flour, "Flour"));
            categoryList.add(new categoryHelperClass(R.drawable.rice, "Rice"));
            categoryList.add(new categoryHelperClass(R.drawable.pulses, "Pulses"));
            categoryList.add(new categoryHelperClass(R.drawable.spices, "Spices"));
            categoryList.add(new categoryHelperClass(R.drawable.oils, "Oils"));
            categoryList.add(new categoryHelperClass(R.drawable.dry_fruits, "Dry Fruits"));

        } else if (category.equals("Dairy Products")) {
            categoryList.add(new categoryHelperClass(R.drawable.milk, "Milk"));
            categoryList.add(new categoryHelperClass(R.drawable.bread_buns, "Bread & Buns"));
        } else if (category.equals("Cleaning & Household")) {
            categoryList.add(new categoryHelperClass(R.drawable.ic_baseline_shopping_cart_24, "Detergents"));
            categoryList.add(new categoryHelperClass(R.drawable.ic_baseline_shopping_cart_24, "Liquid Cleaners"));
        } else if (category.equals("Fruits & Vegetables")) {
            categoryList.add(new categoryHelperClass(R.drawable.ic_baseline_shopping_cart_24, "Fruits"));
            categoryList.add(new categoryHelperClass(R.drawable.ic_baseline_shopping_cart_24, "Vegetables"));
            categoryList.add(new categoryHelperClass(R.drawable.ic_baseline_shopping_cart_24, "Organic"));
        } else if (category.equals("Beverages")) {
            categoryList.add(new categoryHelperClass(R.drawable.tea, "Tea"));
            categoryList.add(new categoryHelperClass(R.drawable.coffee, "Coffee"));
            categoryList.add(new categoryHelperClass(R.drawable.juices, "Juices"));
            categoryList.add(new categoryHelperClass(R.drawable.soft_drinks, "Soft drinks"));

        } else if (category.equals("Personal Care")) {

            categoryList.add(new categoryHelperClass(R.drawable.bath_handwash, "Bath & Handwash"));
            categoryList.add(new categoryHelperClass(R.drawable.oral_care, "Oral Care"));
            categoryList.add(new categoryHelperClass(R.drawable.fragnance_deos, "Fragrances & deos"));
        } else if (category.equals("Snacks")) {
            categoryList.add(new categoryHelperClass(R.drawable.ic_baseline_shopping_cart_24, "Biscuits"));
            categoryList.add(new categoryHelperClass(R.drawable.ic_baseline_shopping_cart_24, "Chips |Namkeens"));
            categoryList.add(new categoryHelperClass(R.drawable.ic_baseline_shopping_cart_24, "Noodles|Pasta"));

        }
        adapter = new subCategoryAdapter(categoryList, this);
        categoryGridRecycler.setAdapter(adapter);


    }


    public void setBestOffers() {
        bestOfferRecycler.setHasFixedSize(true);
        bestOfferRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        if (BestItemList.size() == 0) {
            bestOfferRecycler.setVisibility(View.GONE);
            pBar.setVisibility(View.GONE);
            bestOfferTextView.setVisibility(View.GONE);
        } else {
            int i = 0;
            for (itemHelperClass helperClass1 : myCartList) {

                i = 0;
                for (itemHelperClass helperClass2 : BestItemList) {
                    if (helperClass1.getItemName().equals(helperClass2.getItemName())) {
                        BestItemList.get(i).itemQuantity = helperClass1.getItemQuantity();
                        break;
                    }
                    i++;
                }

            }

            bestOfferAdapter = new HorizontalCardItemAdapter(BestItemList, this, getApplicationContext());
            bestOfferRecycler.setAdapter(bestOfferAdapter);
            bestOfferRecycler.setVisibility(View.VISIBLE);
            pBar.setVisibility(View.GONE);
            bestOfferTextView.setVisibility(View.VISIBLE);

        }
    }


    private final Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.searchBtn2) {
                Intent intent = new Intent(subCategory.this, searchBar.class);

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            return true;
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }


    @Override
    public void onAddBtnClick(int position) {
        myCartList.add(new itemHelperClass(BestItemList.get(position).getItemImage(), BestItemList.get(position).getItemName(), BestItemList.get(position).getItemPrice(), BestItemList.get(position).getDiscount(), 1));
        prefConfig.saveList(getApplicationContext(), myCartList);
    }

    @Override
    public void onIncreaseBtnClick(int position, int value) {
        int i = 0;

        for (itemHelperClass helperClass : myCartList) {

            if (helperClass.getItemName().equals(BestItemList.get(position).getItemName())) {
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

            if (helperClass.getItemName().equals(BestItemList.get(position).getItemName())) {
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
    protected void onResume() {
        super.onResume();

        bestOfferRecycler.setVisibility(View.GONE);
        myCartList = prefConfig.getList(getApplicationContext());

        if (myCartList == null)
            myCartList = new ArrayList<>();


        pBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("admin").document(place).collection("products").document(category).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    itemsJson = document.getString("all product");
                    if (itemsJson != null) {
                        allCategoryItems = prefConfig.getProducts(itemsJson);
                        if (allCategoryItems == null)
                            allCategoryItems = new ArrayList<>();

                        BestItemList = new ArrayList<>();
                        for (itemHelperClass helperClass : allCategoryItems) {
                            if (helperClass.getDiscount() >= 5) {
                                BestItemList.add(helperClass);
                            }
                        }
                        setBestOffers();
                    }else{
                        pBar.setVisibility(View.GONE);
                    }


                }
            }
        });
    }

    @Override
    public void onSubCategoryClick(int position) {
        Intent intent = new Intent(this, itemList.class);
        intent.putExtra("subCategory", categoryList.get(position).getCategoryName());
        intent.putExtra("category", category);
        intent.putExtra("fromBanner", false);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}