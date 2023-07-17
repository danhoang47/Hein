package com.hein.home.filter;

public class ColorViewModel {
    private String name;
    private String colorResourceName;

    private boolean selected;

    public ColorViewModel(String name, String colorResourceName) {
        this.name = name;
        this.colorResourceName = colorResourceName;
    }

    public ColorViewModel(String name, String colorResourceName, boolean selected) {
        this.name = name;
        this.colorResourceName = colorResourceName;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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
