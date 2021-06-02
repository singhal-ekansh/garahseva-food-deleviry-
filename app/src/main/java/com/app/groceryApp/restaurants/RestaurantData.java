package com.app.groceryApp.restaurants;

public class RestaurantData {
    String name, cusine, image, price, category,quantitySelected;

    public RestaurantData() {
    }

    public RestaurantData(String name, String price, String quantitySelected) {
        this.name = name;
        this.price = price;
        this.quantitySelected = quantitySelected;
    }

    public String getName() {
        return name;
    }

    public String getCusine() {
        return cusine;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getQuantitySelected() {
        return quantitySelected;
    }

    public void setQuantitySelected(String quantitySelected) {
        this.quantitySelected = quantitySelected;
    }
}
