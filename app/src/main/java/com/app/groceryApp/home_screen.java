package com.app.groceryApp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class home_screen extends Fragment implements categoryAdapter.onCategoryListener, HorizontalCardItemAdapter.onHorizontalItemListener {
    ImageSlider imageSlider;
    List<SlideModel> slideModelList;
    RecyclerView homeCategoryRecycler, homeOfferRecycler;
    RecyclerView.Adapter homeCategoryAdapter, homeOfferRecyclerAdapter;
    ArrayList<categoryHelperClass> homeCategoryList;
    ArrayList<itemHelperClass> homeOfferItemList, myCartList, allItemList;
    FirebaseFirestore firebaseFirestore;
    String bannerJson, allItemsJson, place = "Vikasnagar";
    ArrayList<bannerLinks> links;
    ProgressBar sliderProgressBar, homeDealProgress;
    RelativeLayout hasCurrentOrderOrNotView;
    ArrayList<String> bannerProducts;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);

        imageSlider = view.findViewById(R.id.imageSliderView);
        homeCategoryRecycler = view.findViewById(R.id.homeCategory);
        homeOfferRecycler = view.findViewById(R.id.homeOfferWeekRecycler);
        homeCategoryRecycler.setFocusable(false);
        homeOfferRecycler.setFocusable(false);
        sliderProgressBar = view.findViewById(R.id.sliderProgress);
        homeDealProgress = view.findViewById(R.id.homeDealsProgressBar);
        hasCurrentOrderOrNotView = view.findViewById(R.id.hasCurrentOrderOrNot);
        sliderProgressBar.setVisibility(View.VISIBLE);
        homeDealProgress.setVisibility(View.VISIBLE);
        firebaseFirestore = FirebaseFirestore.getInstance();

        place = prefConfig.getDeliveryLocation(getContext());

        if (prefConfig.hasCurrentOrder(getContext()))
            hasCurrentOrderOrNotView.setVisibility(View.VISIBLE);

        else
            hasCurrentOrderOrNotView.setVisibility(View.GONE);

        setHomeCategoryRecycler();

        firebaseFirestore.collection("admin").document(place).collection("banners").document("links").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    bannerJson = document.getString("linkJson");

                    links = prefConfig.getBannerList(bannerJson);

                    slideModelList = new ArrayList<>();

                    if (links != null) {
                        for (bannerLinks bannerLinks : links) {
                            slideModelList.add(new SlideModel(bannerLinks.getLink(), ScaleTypes.FIT));

                        }
                    }

                    bannerProducts = new ArrayList<>();

                    for (int i = 1; i <= links.size(); i++) {
                        bannerProducts.add(document.getString("linkProducts" + i));
                    }

                    setSlider();

                }
            }
        });


        hasCurrentOrderOrNotView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), myOrders.class));
                getActivity().overridePendingTransition(0, 0);
            }
        });

        return view;
    }

    private void setSlider() {
        imageSlider.setImageList(slideModelList, ScaleTypes.FIT);
        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                if (bannerProducts.get(i).length() != 0) {
                    Intent intent = new Intent(getActivity(), itemList.class);
                    intent.putExtra("fromBanner", true);
                    intent.putExtra("bannerProducts", bannerProducts.get(i));
                    intent.putExtra("subCategory", "Best Deals");
                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);
                }
            }
        });

        sliderProgressBar.setVisibility(View.GONE);

    }

    private void setHomePageOffersRecycler() {
        homeOfferRecycler.setHasFixedSize(true);
        homeOfferRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        int i;
        for (itemHelperClass helperClass1 : myCartList) {

            i = 0;
            for (itemHelperClass helperClass2 : homeOfferItemList) {
                if (helperClass1.getItemName().equals(helperClass2.getItemName())) {
                    homeOfferItemList.get(i).itemQuantity = helperClass1.getItemQuantity();
                    break;
                }
                i++;
            }

        }
        homeOfferRecyclerAdapter = new HorizontalCardItemAdapter(homeOfferItemList, this, getContext());
        homeOfferRecycler.setAdapter(homeOfferRecyclerAdapter);
        homeDealProgress.setVisibility(View.GONE);
        homeOfferRecycler.setVisibility(View.VISIBLE);
    }

    private void setHomeCategoryRecycler() {
        homeCategoryRecycler.setHasFixedSize(true);

        homeCategoryRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));

        homeCategoryList = new ArrayList<>();
        homeCategoryList.add(new categoryHelperClass(R.drawable.drygoods, "Dry & Baking Goods"));
        homeCategoryList.add(new categoryHelperClass(R.drawable.dairy, "Dairy Products"));
        homeCategoryList.add(new categoryHelperClass(R.drawable.cleaning, "Cleaning & Household"));
        homeCategoryList.add(new categoryHelperClass(R.drawable.fruits, "Fruits & Vegetables"));
        homeCategoryList.add(new categoryHelperClass(R.drawable.beverages, "Beverages"));
        homeCategoryList.add(new categoryHelperClass(R.drawable.personalcare, "Personal Care"));
        homeCategoryList.add(new categoryHelperClass(R.drawable.snacks, "Snacks"));
        homeCategoryList.add(new categoryHelperClass(R.drawable.cleaning, "Cleaning & Household"));
        homeCategoryList.add(new categoryHelperClass(R.drawable.fruits, "Fruits & Vegetables"));


        homeCategoryAdapter = new categoryAdapter(homeCategoryList, this);
        homeCategoryRecycler.setAdapter(homeCategoryAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onCategoryClick(int position) {
        Intent intent = new Intent(getActivity(), subCategory.class);
        intent.putExtra("title", homeCategoryList.get(position).getCategoryName());
        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    @Override
    public void onAddBtnClick(int position) {
        myCartList.add(new itemHelperClass(homeOfferItemList.get(position).getItemImage(), homeOfferItemList.get(position).getItemName(), homeOfferItemList.get(position).getItemPrice(), homeOfferItemList.get(position).getDiscount(), 1));
        prefConfig.saveList(getContext(), myCartList);
    }

    @Override
    public void onIncreaseBtnClick(int position, int value) {
        int i = 0;

        for (itemHelperClass helperClass : myCartList) {

            if (helperClass.getItemName().equals(homeOfferItemList.get(position).getItemName())) {
                myCartList.get(i).itemQuantity = value;
                break;
            }
            i++;
        }
        prefConfig.saveList(getContext(), myCartList);

    }

    @Override
    public void onDecreaseBtnClick(int position, int value) {
        int i = 0;

        for (itemHelperClass helperClass : myCartList) {

            if (helperClass.getItemName().equals(homeOfferItemList.get(position).getItemName())) {
                if (value != 0)
                    myCartList.get(i).itemQuantity = value;
                else
                    myCartList.remove(i);

                break;
            }
            i++;
        }
        prefConfig.saveList(getContext(), myCartList);
    }

    @Override
    public void onResume() {
        super.onResume();

        homeOfferRecycler.setVisibility(View.GONE);
        myCartList = prefConfig.getList(getContext());

        if (myCartList == null)
            myCartList = new ArrayList<>();

        firebaseFirestore.collection("admin").document(place).collection("products").document("allProducts").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    allItemsJson = document.getString("data");
                    homeOfferItemList = new ArrayList<>();

                    allItemList = prefConfig.getProducts(allItemsJson);


                    for (itemHelperClass helperClass : allItemList) {
                        if (helperClass.getDiscount() >= 5) {
                            homeOfferItemList.add(helperClass);
                        }
                    }
                    setHomePageOffersRecycler();

                }
            }
        });


    }
}