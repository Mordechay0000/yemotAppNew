package com.mordechay.yemotapp.ui.programmatically.list;

public class ItemData {

    int image;
    String txt1;
    String txt2;
    String txt3;
    String txt4;
    String txt5;


    // create constructor to set the values for all the parameters of the each single view
    public ItemData(int image, String txt1, String txt2, String txt3, String txt4, String txt5) {
        this.image = image;
        this.txt1 = txt1;
        this.txt2 = txt2;
        this.txt3 = txt3;
        this.txt4 = txt4;
        this.txt5 = txt5;
    }



    public int getImage() {
        return image;
    }

    public String getTxt1() {
        return txt1;
    }

    public String getTxt2() {
        return txt2;
    }

    public String getTxt3() {
        return txt3;
    }

    public String getTxt4() {
        return txt4;
    }

    public String getTxt5() {
        return txt5;
    }
}
