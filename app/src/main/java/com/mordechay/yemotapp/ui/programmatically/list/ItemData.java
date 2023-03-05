package com.mordechay.yemotapp.ui.programmatically.list;

public class ItemData {

    int image;
    String[] txt;
    String[] txtInfo;


    // create constructor to set the values for all the parameters of the each single view
    public ItemData(int image, String[] txt) {
        this.image = image;
        this.txt = txt;
    }

    public ItemData(int image, String[] txt, String[] txtInfo) {
        this.image = image;
        this.txt = txt;
        this.txtInfo = txtInfo;
    }


    public int getImage() {
        return image;
    }

    public String[] getTxt() {
        return txt;
    }
    public String[] getTxtInfo() {
        return txtInfo;
    }

}
