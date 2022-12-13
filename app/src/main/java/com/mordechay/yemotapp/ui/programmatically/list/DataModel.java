package com.mordechay.yemotapp.ui.programmatically.list;

import android.util.Log;

import com.mordechay.yemotapp.R;

import java.util.ArrayList;

public class DataModel {

    ArrayList<Object> ary = new ArrayList<>();


    // create constructor to set the values for all the parameters of the each single view
    public DataModel(ArrayList<Object> ary) {
        this.ary = ary;
    }

    // getter method for returning the ID of the imageview
    public int getImage() {
        if (ary.get(0) == null)
            return R.drawable.ic_baseline_delete_24;

        for (int i = 0; i < ary.size(); i++) {
            Log.e("tag", String.valueOf(ary.get(i)));
        }
        return Integer.parseInt((String) ary.get(0));
    }

    // getter method for returning the ID of the imageview
    public Object getArray() {
        return ary;
    }
}
