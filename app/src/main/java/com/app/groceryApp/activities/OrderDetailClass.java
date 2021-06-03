package com.app.groceryApp.activities;

public class OrderDetailClass {
    String delivery_address, order_detail, total_amount, user_id, timestamp, status, restaurant_id, order_id,restaurant_name;


    public String getDelivery_address() {
        return delivery_address;
    }

    public String getOrder_detail() {
        return order_detail;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public void setOrder_detail(String order_detail) {
        this.order_detail = order_detail;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }
}
