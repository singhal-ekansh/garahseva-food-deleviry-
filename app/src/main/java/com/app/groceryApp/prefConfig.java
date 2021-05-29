package com.app.groceryApp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class prefConfig {

    public static final String FCM_KEY = "AAAAyvy_7rs:APA91bG01HJUXNUT7n6pRma-EWtjLt9bMzGc7jg8GEoUmlHYjHw6tu_IhcNzS84ETDlrWFnxDqJV6KLzT-fQygh1AFQJ1lJofBdBjJ44nmzO1sfzsWNXoB83chP-oXKTrW2-nK02B2jF";
    public static final String FCM_TOPIC = "PUSH_NOTIFICATIONS_TO_ADMIN";

    public static void saveList(Context context, ArrayList<itemHelperClass> list) {

        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("myCartList", jsonString);
        editor.apply();

    }

    public static ArrayList<itemHelperClass> getList(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        String jsonString = preferences.getString("myCartList", "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<itemHelperClass>>() {
        }.getType();

        ArrayList<itemHelperClass> list;
        list = gson.fromJson(jsonString, type);

        return list;
    }

    public static ArrayList<bannerLinks> getBannerList(String bannerJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<bannerLinks>>() {
        }.getType();

        ArrayList<bannerLinks> list;
        list = gson.fromJson(bannerJson, type);

        return list;
    }

    public static ArrayList<itemHelperClass> getProducts(String itemsJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<itemHelperClass>>() {
        }.getType();

        ArrayList<itemHelperClass> list;
        list = gson.fromJson(itemsJson, type);

        return list;
    }

    public static void saveAddress(Context context, String name, String phone, String postalCode, String address, String landmark) {
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("name", name);
        addressMap.put("contact", phone);
        addressMap.put("postal code", postalCode);
        addressMap.put("delivery address", address);
        addressMap.put("landmark", landmark);

        Gson gson = new Gson();
        String jsonString = gson.toJson(addressMap);

        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("myAddress", jsonString);
        editor.apply();

    }

    public static Map<String, Object> getAddressMap(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        String jsonString = preferences.getString("myAddress", "");

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();

        Map<String, Object> addressMap = gson.fromJson(jsonString, type);
        return addressMap;

    }

    public static String getOrderJson(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        String jsonString = preferences.getString("myCartList", "");

        return jsonString;
    }

    public static String getAddressJson(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        String jsonString = preferences.getString("myAddress", "");

        return jsonString;
    }


    public static void onPlacingOrder(Context context) {
// here empty my cart list in shared preferences
        ArrayList<itemHelperClass> list;
        list = new ArrayList<>();

        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("myCartList", jsonString);
        editor.putBoolean("HasCurrentOrderOrNot", true);
        editor.apply();

    }

    public static void setDeliverLocation(Context context, String place) {
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("deliveryArea", place);
        editor.apply();
    }

    public static String getDeliveryLocation(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        String place = preferences.getString("deliveryArea", "Vikasnagar");

        return place;
    }

    public static void removeHasCurrentOrder(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("HasCurrentOrderOrNot", false);
        editor.apply();
    }

    public static boolean hasCurrentOrder(Context context) {
        boolean hasOrNot;
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        hasOrNot = preferences.getBoolean("HasCurrentOrderOrNot", false);
        return hasOrNot;
    }

    public static void clearList(Context context) {
        ArrayList<itemHelperClass> list;
        list = new ArrayList<>();

        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("myCartList", jsonString);
        editor.apply();
    }

    public static void registerPref(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterPref(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences preferences = context.getSharedPreferences("myDetails", Context.MODE_PRIVATE);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
