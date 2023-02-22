package com.mordechay.yemotapp.ui.programmatically.list;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.ui.programmatically.errors.errorHandler;
import com.mordechay.yemotapp.ui.programmatically.list.DataModel;

import java.util.ArrayList;




public class newList {


    public void newList() {

    }

    public ArrayList getAdapter(Activity act, ArrayList<ArrayList<String>> ary) {
        ArrayList<DataModel> adapter = new ArrayList<DataModel>();
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
            adapter.add(new DataModel(aryyy.get(i)));
        }

        return adapter;
    }
}






