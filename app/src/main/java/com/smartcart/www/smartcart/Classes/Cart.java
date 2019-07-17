package com.smartcart.www.smartcart.Classes;

/**
 * Created by shyattoun on 19.12.2018.
 */

public class Cart  {

    private String cartID;
    private String date;
    private String location;
    private double price;

    public Cart() {
    }

    public Cart(String cartID, String date, String location, double price) {
        this.cartID = cartID;
        this.date = date;
        this.location = location;
        this.price = price;
    }

    public String getCartID() {
        return cartID;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public double getPrice() {
        return price;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartID='" + cartID + '\'' +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", price=" + price +
                '}';
    }
}
