package com.hein.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Product implements Serializable {
    private String id;
    private String brand;
    private String category;
    private List<String> classifications;
    private List<String> colors;
    private List<String> images;
    private String name;
    private double price;
    private int quantity;
    private String type;
    private Map<String, Integer> sizes;

    public Product(String id, String brand, String category, List<String> classifications, List<String> colors, List<String> images, String name, double price, int quantity, String type, Map<String, Integer> sizes) {
        this.id = id;
        this.brand = brand;
        this.category = category;
        this.classifications = classifications;
        this.colors = colors;
        this.images = images;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
        this.sizes = sizes;
    }

    public Product() {

    }

    public Product(String brand, String category, List<String> classifications,
                   List<String> colors, List<String> images, String name,
                   double price, int quantity, String type, Map<String,
                    Integer> sizes) {
        this.brand = brand;
        this.category = category;
        this.classifications = classifications;
        this.colors = colors;
        this.images = images;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
        this.sizes = sizes;
    }

    public Product(String brand, String category, List<String> classifications,
                   List<String> colors, String name,
                   double price, int quantity, String type, Map<String,
            Integer> sizes) {
        this.brand = brand;
        this.category = category;
        this.classifications = classifications;
        this.colors = colors;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
        this.sizes = sizes;
    }

    public Map<String, Integer> getSizes() {
        return sizes;
    }

    public void setSizes(Map<String, Integer> sizes) {
        this.sizes = sizes;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getClassifications() {
        return classifications;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClassifications(List<String> classifications) {
        this.classifications = classifications;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Product{" +
                "brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", classifications=" + classifications +
                ", colors=" + colors +
                ", images=" + images +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", type='" + type + '\'' +
                ", sizes=" + sizes +
                '}';
    }
}
