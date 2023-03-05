package com.mordechay.yemotapp.ui.programmatically.list;

public class ItemData {

    int image;
    String[] txt;


    // create constructor to set the values for all the parameters of the each single view
    public ItemData(int image, String[] txt) {
        this.image = image;
        this.txt = txt;
    }



    public int getImage() {
        return image;
    }

    public String[] getTxt() {
        return txt;
    }
}
