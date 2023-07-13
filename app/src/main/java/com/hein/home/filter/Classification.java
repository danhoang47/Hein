package com.hein.home.filter;

import android.graphics.drawable.Drawable;

public class Classification {
    private int id;
    private Drawable drawableIcon;
    private String title;

    public Classification(int id, Drawable drawableIcon, String title) {
        this.id = id;
        this.drawableIcon = drawableIcon;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Drawable getDrawableIcon() {
        return drawableIcon;
    }

    public void setDrawableIcon(Drawable drawableIcon) {
        this.drawableIcon = drawableIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
