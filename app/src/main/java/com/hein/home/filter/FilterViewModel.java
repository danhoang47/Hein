package com.hein.home.filter;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FilterViewModel extends ViewModel {
    private Set<String> genders;
    private double priceFrom;
    private double priceTo;
    private Set<String> sizes;
    private Set<String> colors;
    private Set<String> types;
    public MutableLiveData<Boolean> filterState = new MutableLiveData<>();
    public FilterViewModel() {
        this.genders = new HashSet<>();
        this.sizes = new HashSet<>();
        this.colors = new HashSet<>();
        this.types = new HashSet<>();
        filterState.postValue(false);
    }

    public void unsetAllProperties(double minPrice, double maxPrice) {
        this.genders = new HashSet<>();
        this.sizes = new HashSet<>();
        this.colors = new HashSet<>();
        this.types = new HashSet<>();
        filterState.postValue(false);
        this.priceFrom = minPrice;
        this.priceTo = maxPrice;
    }

    public Set<String> getGenders() {
        return genders;
    }

    public void setGenders(Set<String> genders) {
        this.genders = genders;
    }

    public double getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(double priceFrom) {
        this.priceFrom = priceFrom;
    }

    public double getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(double priceTo) {
        this.priceTo = priceTo;
    }

    public Set<String> getSizes() {
        return sizes;
    }

    public void setSizes(Set<String> sizes) {
        this.sizes = sizes;
    }

    public Set<String> getColors() {
        return colors;
    }

    public void setColors(Set<String> colors) {
        this.colors = colors;
    }

    public Set<String> getTypes() { return this.types; }

    public boolean isAllPropertiesUnset() {
        Field[] fields = this.getClass().getDeclaredFields();

        return Arrays.stream(fields).allMatch(field -> {
            field.setAccessible(true);
            Class fieldType = field.getType();

            try {
                if (fieldType.isAssignableFrom(MutableLiveData.class) ||
                        Modifier.isStatic(field.getModifiers())) {
                    return true;
                } else if (fieldType.isAssignableFrom(Set.class)) {
                    Set fieldValue = (Set) field.get(this);
                    Log.i("MSG", field.getName() + ": " + (fieldValue.size() == 0));
                    return fieldValue.size() == 0;
                } else if (fieldType.isAssignableFrom(double.class)) {
                    Double value = ((Number) field.get(this)).doubleValue();
                    Log.i("MSG", field.getName() + ": " + (((Number) field.get(this)).doubleValue() == 0.0) + "");
                    return value != null ? value == 0 : true;
                } else {
                    Log.i("MSG", fieldType.getName());
                }
            } catch (Exception e) {
                Log.i("Exception", e.getMessage());
            }
            return false;
        });
    }

    @Override
    public String toString() {
        return "FilterViewModel{" +
                "genders=" + genders.toString() +
                ", priceFrom=" + priceFrom +
                ", priceTo=" + priceTo +
                ", sizes=" + sizes.toString() +
                ", colors=" + colors.toString() +
                ", types=" + types.toString() +
                '}';
    }
}
