package com.hein.entity;

public class Booking {
    private String id;
    private String userId;
    private String productId;
    private int quantity;
    private String size;
    private String color;
    private String timestamp;

    private int status;

    private float totalPrice;

    private String productName;
    // type : 0 - cart, 1 - booking
    private int type;



    /*public Booking(String bookingId, String userId, String productId, int quantity, String size, String color, String timeStamp) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
        this.timestamp = timeStamp;
    }*/

    public Booking(String userId, String productId, int quantity, String size, String color, String timestamp, float totalPrice, String productName, int status) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
        this.timestamp = timestamp;
        this.totalPrice = totalPrice;
        this.productName = productName;
        this.status = status;
    }


    public Booking() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timeStamp) {
        this.timestamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", size='" + size + '\'' +
                ", color='" + color + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", totalPrice=" + totalPrice +
                ", productName='" + productName + '\'' +
                '}';
    }
}

