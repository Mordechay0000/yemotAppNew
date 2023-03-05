package com.mordechay.yemotapp.ui.programmatically.list;

import android.app.Activity;

import com.mordechay.yemotapp.ui.programmatically.errors.errorHandler;

import java.util.ArrayList;




public class newList {


    public void newList() {

    }

    public ArrayList getAdapter(Activity act, ArrayList<ArrayList<String>> ary) {
        ArrayList<ItemData> adapter = new ArrayList<ItemData>();
        ArrayList<ArrayList<Object>> aryyy = new ArrayList<>();

        for(int a = 0; a < ary.size(); a++) {
            for (int b = 0; b < ary.get(a).size(); b++) {

                if(a == 0 & aryyy.size() != ary.get(0).size()) {
                    aryyy.add(new ArrayList<>());
                }

                try {
                    aryyy.get(b).add(ary.get(a).get(b));
                }catch (IndexOutOfBoundsException e){
                    new errorHandler(act, e);
                }
            }
        }
        for (int i = 0; i < aryyy.size(); i++) {
            //adapter.add(new ItemData(aryyy.get(i)));
        }

        return adapter;
    }
}






