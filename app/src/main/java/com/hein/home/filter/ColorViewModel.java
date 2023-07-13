package com.hein.home.filter;

public class ColorViewModel {
    private String name;
    private String colorResourceName;

    public ColorViewModel(String name, String colorResourceName) {
        this.name = name;
        this.colorResourceName = colorResourceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorResourceName() {
        return colorResourceName;
    }

    public void setColorResource(String colorResourceName) {
        this.colorResourceName = colorResourceName;
    }
}
