package com.app.groceryApp;

import android.app.Activity;
import android.content.Context;

public class itemHelperClass {

    int itemQuantity, itemPrice, discount;
    String itemName, itemImage;


    public String getItemImage() {

        return itemImage;
    }


    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public itemHelperClass(String itemImage, String itemName, int itemPrice, int discount, int itemQuantity) {
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.discount = discount;
    }
}
