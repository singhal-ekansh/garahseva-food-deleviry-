package com.app.garahseva.restaurants;

public class RestaurantData {
    String name, cusine, image, price, category,quantitySelected,deliveryFee,freeDeliveryPrice;
    boolean isClosed;

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

    public boolean getIsClosed() {
        return isClosed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCusine(String cusine) {
        this.cusine = cusine;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setIsClosed(boolean closed) {
        isClosed = closed;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getFreeDeliveryPrice() {
        return freeDeliveryPrice;
    }

    public void setFreeDeliveryPrice(String freeDeliveryPrice) {
        this.freeDeliveryPrice = freeDeliveryPrice;
    }
}
