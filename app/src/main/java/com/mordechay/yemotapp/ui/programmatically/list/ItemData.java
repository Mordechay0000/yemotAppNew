package com.mordechay.yemotapp.ui.programmatically.list;

import android.graphics.drawable.Drawable;

public class ItemData {

    Drawable image;
    String[] txt;
    String[] txtInfo;


    // create constructor to set the values for all the parameters of the each single view
    public ItemData(Drawable image, String[] txt) {
        this.image = image;
        this.txt = txt;
    }

    public ItemData(Drawable image, String[] txt, String[] txtInfo) {
        this.image = image;
        this.txt = txt;
        this.txtInfo = txtInfo;
    }


    public Drawable getImage() {
        return image;
    }

    public String[] getTxt() {
        return txt;
    }
    public String[] getTxtInfo() {
        return txtInfo;
    }

}
