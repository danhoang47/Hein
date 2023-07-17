package com.hein.shoppingcart;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hein.entity.Booking;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {
    MutableLiveData<Integer> totalPrice = new MutableLiveData<>();
    MutableLiveData<Boolean> isCartItemChanged = new MutableLiveData<>();
    public MutableLiveData<List<Booking>> orders = new MutableLiveData<>();
    public MutableLiveData<List<String>> deletedOrders = new MutableLiveData<>();

    public void setTotalPrice(int totalPrice) {
        this.totalPrice.postValue(totalPrice);
    }

    public void setIsCartItemChanged(boolean status) {
        this.isCartItemChanged.postValue(status);
    }

    public Boolean isCartItemChanged() {
        return isCartItemChanged.getValue();
    }

    public Integer getTotalPrice() {
        return this.totalPrice.getValue();
    }

    public CartViewModel() {
        super();
        totalPrice.postValue(0);
        isCartItemChanged.postValue(false);
        orders.postValue(new ArrayList<>());
        deletedOrders.postValue(new ArrayList<>());
    }
}
