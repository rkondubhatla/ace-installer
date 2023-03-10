package com.sk.aceinstaller2;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.io.Serializable;

public class ApkFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String packageName;
    private Drawable icon;
    private boolean isSelected;
    private boolean isInstalled;

    public ApkFile() {

    }

    public ApkFile(String name) {
        this.name = name;
    }

    public ApkFile(String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
    }

    public ApkFile(String name, boolean isSelected) {

        this.name = name;
        this.isSelected = isSelected;
    }

    public ApkFile(String name, Drawable icon, boolean isSelected) {

        this.name = name;
        this.icon = icon;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public int installedButtonView() {
        if (isInstalled) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    public void setInstalled(boolean isInstalled) {
        this.isInstalled = isInstalled;
    }
}
