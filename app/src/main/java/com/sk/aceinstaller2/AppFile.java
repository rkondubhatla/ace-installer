package com.sk.aceinstaller2;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String packageName;
    private String name;
    private Drawable icon;

    public AppFile() {

    }

    public AppFile(String name, String packageName) {
        this.name = name;
    }

    public AppFile(String name, String packageName, Drawable icon) {
        this.name = name;
        this.icon = icon;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
