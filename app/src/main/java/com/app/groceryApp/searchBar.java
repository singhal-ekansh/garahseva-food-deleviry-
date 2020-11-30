package com.app.groceryApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class searchBar extends AppCompatActivity implements itemAdapter.onItemListener {

    RecyclerView searchRecyclerView;
    RecyclerView.Adapter searchAdapter;
    ArrayList<itemHelperClass> allItemList, myCartList, searchList;
    SearchView searchView;
    TextView notFoundText;
    boolean isCrossed = true;
    FirebaseFirestore store;
    String allItemsJson,place;
    ProgressBar searchProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);
        searchRecyclerView = findViewById(R.id.searchRecycler);
        searchView = findViewById(R.id.searchBar);
        notFoundText = findViewById(R.id.notFoundTxt);
        notFoundText.setVisibility(View.GONE);
        searchProgressBar = findViewById(R.id.searchProgress);
        store = FirebaseFirestore.getInstance();
        place = prefConfig.getDeliveryLocation(getApplicationContext());
        myCartList = prefConfig.getList(getApplicationContext());

        if (myCartList == null)
            myCartList = new ArrayList<>();


        searchList = new ArrayList<>();
        allItemList = new ArrayList<>();

        store.collection("admin").document(place).collection("products").document("allProducts").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    allItemsJson = document.getString("data");

                    allItemList = prefConfig.getProducts(allItemsJson);
                    setRecycler();

                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProgressBar.setVisibility(View.VISIBLE);
                searchList.clear();

                for (itemHelperClass obj : allItemList) {
                    if (obj.getItemName().toLowerCase().contains(query.toLowerCase())) {
                        searchList.add(obj);

                    }
                }
                if (searchList.size() == 0)
                    isCrossed = false;

                setRecycler();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    searchList.clear();
                    isCrossed = true;
                }
                return false;
            }

        });


    }

    void setRecycler() {


        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        if (searchList.size() != 0) {
            int i = 0;
            for (itemHelperClass helperClass1 : myCartList) {

                i = 0;
                for (itemHelperClass helperClass2 : searchList) {
                    if (helperClass1.getItemName().equals(helperClass2.getItemName())) {
                        searchList.get(i).itemQuantity = helperClass1.getItemQuantity();
                        break;
                    }
                    i++;
                }

            }
        }


        searchAdapter = new filterAdapter(searchList, this, getApplicationContext());
        searchRecyclerView.setAdapter(searchAdapter);

        if (searchList.size() == 0 && !isCrossed)
            notFoundText.setVisibility(View.VISIBLE);
        else
            notFoundText.setVisibility(View.GONE);

        searchProgressBar.setVisibility(View.GONE);
    }


    public class filterAdapter extends itemAdapter implements Filterable {

        public filterAdapter(ArrayList<itemHelperClass> itemList, itemAdapter.onItemListener onItemListener, Context context) {
            super(itemList, onItemListener, context);
        }


        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    //filter logic


                    return null;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    notifyDataSetChanged();
                }
            };
        }

    }


    @Override
    public void onAddBtnClick(int position) {


        myCartList.add(new itemHelperClass(searchList.get(position).getItemImage(), searchList.get(position).getItemName(), searchList.get(position).getItemPrice(), searchList.get(position).getDiscount(), 1));
        prefConfig.saveList(getApplicationContext(), myCartList);
    }

    @Override
    public void onIncreaseBtnClick(int position, int value) {
        int i = 0;

        for (itemHelperClass helperClass : myCartList) {

            if (helperClass.getItemName().equals(searchList.get(position).getItemName())) {
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

            if (helperClass.getItemName().equals(searchList.get(position).getItemName())) {
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